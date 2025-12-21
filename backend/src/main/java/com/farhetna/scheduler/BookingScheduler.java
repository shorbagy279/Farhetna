// ============= SCHEDULED TASKS =============

package com.farhetna.scheduler;

import com.farhetna.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {
    
    private final BookingService bookingService;
    private final NotificationService notificationService;
    
    // Run every 5 minutes to check for expired bookings
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void handleExpiredBookings() {
        log.info("Checking for expired bookings...");
        try {
            bookingService.handleExpiredBookings();
        } catch (Exception e) {
            log.error("Error handling expired bookings", e);
        }
    }
    
    // Run every 15 minutes to send expiring soon notifications
    @Scheduled(fixedRate = 900000) // 15 minutes
    public void sendExpiringCodeNotifications() {
        log.info("Checking for expiring booking codes...");
        try {
            notificationService.sendExpiringCodeNotifications();
        } catch (Exception e) {
            log.error("Error sending expiring code notifications", e);
        }
    }
    
    // Run daily at 1 AM to mark completed bookings as eligible for rating
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateRatingEligibility() {
        log.info("Updating rating eligibility for completed bookings...");
        try {
            bookingService.updateRatingEligibility();
        } catch (Exception e) {
            log.error("Error updating rating eligibility", e);
        }
    }
    
    // Run every hour to sync offline bookings
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void syncOfflineBookings() {
        log.info("Syncing offline bookings...");
        try {
            bookingService.syncOfflineBookings();
        } catch (Exception e) {
            log.error("Error syncing offline bookings", e);
        }
    }
}

