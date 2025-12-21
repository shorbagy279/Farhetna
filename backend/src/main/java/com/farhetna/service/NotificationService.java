package com.farhetna.service;

import com.farhetna.model.*;
import com.farhetna.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final BookingCodeRepository bookingCodeRepository;
    
    @Transactional
    public void sendNewBookingNotification(Booking booking) {
        Notification notification = new Notification();
        notification.setRecipient(booking.getHall().getOwner().getUser());
        notification.setType(NotificationType.NEW_BOOKING_REQUEST);
        notification.setTitleAr("طلب حجز جديد");
        notification.setTitleEn("New Booking Request");
        notification.setMessageAr(String.format("لديك طلب حجز جديد لتاريخ %s", 
            booking.getEventDate()));
        notification.setMessageEn(String.format("You have a new booking request for %s", 
            booking.getEventDate()));
        notification.setRelatedBooking(booking);
        notification.setRelatedHall(booking.getHall());
        notification.setDeepLink("/bookings/" + booking.getId());
        
        notificationRepository.save(notification);
        log.info("New booking notification sent for booking {}", booking.getId());
    }
    
    @Transactional
    public void sendBookingModifiedNotification(Booking booking) {
        Notification notification = new Notification();
        notification.setRecipient(booking.getHall().getOwner().getUser());
        notification.setType(NotificationType.BOOKING_MODIFIED);
        notification.setTitleAr("تم تعديل الحجز");
        notification.setTitleEn("Booking Modified");
        notification.setMessageAr(String.format("تم تعديل الحجز رقم %d - يرجى مراجعة التفاصيل الجديدة", 
            booking.getId()));
        notification.setMessageEn(String.format("Booking #%d has been modified - please review new details", 
            booking.getId()));
        notification.setRelatedBooking(booking);
        notification.setRelatedHall(booking.getHall());
        notification.setDeepLink("/bookings/" + booking.getId());
        
        notificationRepository.save(notification);
        log.info("Booking modified notification sent for booking {}", booking.getId());
    }
    
    @Transactional
    public void sendBookingConfirmedNotification(Booking booking) {
        // Notification to customer
        Notification customerNotif = new Notification();
        customerNotif.setRecipient(booking.getCustomer().getUser());
        customerNotif.setType(NotificationType.BOOKING_CONFIRMED);
        customerNotif.setTitleAr("تم تأكيد الحجز");
        customerNotif.setTitleEn("Booking Confirmed");
        customerNotif.setMessageAr(String.format("تم تأكيد حجزك في %s لتاريخ %s", 
            booking.getHall().getNameAr(), booking.getEventDate()));
        customerNotif.setMessageEn(String.format("Your booking at %s for %s has been confirmed", 
            booking.getHall().getNameEn(), booking.getEventDate()));
        customerNotif.setRelatedBooking(booking);
        customerNotif.setRelatedHall(booking.getHall());
        customerNotif.setDeepLink("/bookings/" + booking.getId());
        
        notificationRepository.save(customerNotif);
        log.info("Booking confirmed notification sent to customer for booking {}", booking.getId());
    }
    
    @Transactional
    public void sendBookingExpiredNotification(Booking booking) {
        Notification notification = new Notification();
        notification.setRecipient(booking.getCustomer().getUser());
        notification.setType(NotificationType.BOOKING_EXPIRED);
        notification.setTitleAr("انتهت صلاحية الحجز");
        notification.setTitleEn("Booking Expired");
        notification.setMessageAr(String.format("انتهت صلاحية كود الحجز لتاريخ %s. يمكنك إنشاء حجز جديد.", 
            booking.getEventDate()));
        notification.setMessageEn(String.format("Your booking code for %s has expired. You can create a new booking.", 
            booking.getEventDate()));
        notification.setRelatedBooking(booking);
        notification.setRelatedHall(booking.getHall());
        
        notificationRepository.save(notification);
        log.info("Booking expired notification sent for booking {}", booking.getId());
    }
    
    @Transactional
    public void sendExpiringCodeNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursFromNow = now.plusHours(2);
        
        List<BookingCode> expiringCodes = bookingCodeRepository
            .findCodesExpiringBetween(now, twoHoursFromNow);
        
        for (BookingCode code : expiringCodes) {
            Booking booking = code.getBooking();
            
            Notification notification = new Notification();
            notification.setRecipient(booking.getCustomer().getUser());
            notification.setType(NotificationType.CODE_EXPIRING_SOON);
            notification.setTitleAr("تنبيه: كود الحجز سينتهي قريباً");
            notification.setTitleEn("Alert: Booking Code Expiring Soon");
            notification.setMessageAr(String.format("كود حجزك سينتهي خلال ساعتين. يرجى زيارة القاعة قبل %s", 
                code.getExpiresAt()));
            notification.setMessageEn(String.format("Your booking code will expire in 2 hours. Please visit the hall before %s", 
                code.getExpiresAt()));
            notification.setRelatedBooking(booking);
            notification.setRelatedHall(booking.getHall());
            notification.setDeepLink("/bookings/" + booking.getId());
            
            notificationRepository.save(notification);
        }
        
        log.info("Expiring code notifications sent for {} bookings", expiringCodes.size());
    }
    
    @Transactional
    public void sendRatingRequestNotification(Booking booking) {
        Notification notification = new Notification();
        notification.setRecipient(booking.getCustomer().getUser());
        notification.setType(NotificationType.RATING_REQUEST);
        notification.setTitleAr("قيم تجربتك");
        notification.setTitleEn("Rate Your Experience");
        notification.setMessageAr(String.format("كيف كانت تجربتك في %s؟ شارك رأيك الآن", 
            booking.getHall().getNameAr()));
        notification.setMessageEn(String.format("How was your experience at %s? Share your feedback", 
            booking.getHall().getNameEn()));
        notification.setRelatedBooking(booking);
        notification.setRelatedHall(booking.getHall());
        notification.setDeepLink("/bookings/" + booking.getId() + "/rate");
        
        notificationRepository.save(notification);
        log.info("Rating request notification sent for booking {}", booking.getId());
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId, Boolean unreadOnly) {
        return notificationRepository.findByUserIdAndReadStatus(userId, unreadOnly);
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}