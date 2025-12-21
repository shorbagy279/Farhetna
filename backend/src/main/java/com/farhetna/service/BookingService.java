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
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final BookingCodeService bookingCodeService;
    private final BookingModificationRepository modificationRepository;
    private final BlockedDateRepository blockedDateRepository;
    private final NotificationService notificationService;
    private final HallRepository hallRepository;
    
    private static final int MAX_MODIFICATIONS = 3;
    
    @Transactional
    public Booking createBooking(Customer customer, Hall hall, HallPackage selectedPackage,
                                  List<AddOn> addOns, LocalDate eventDate) {
        
        // Check if date is available
        if (!isDateAvailable(hall.getId(), eventDate)) {
            throw new RuntimeException("Selected date is not available");
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHall(hall);
        booking.setSelectedPackage(selectedPackage);
        booking.setSelectedAddOns(addOns);
        booking.setEventDate(eventDate);
        booking.setStatus(BookingStatus.PENDING);
        
        // Lock prices
        booking.setLockedPackagePrice(selectedPackage.getPrice());
        
        List<LockedAddOnPrice> lockedAddOnPrices = addOns.stream()
            .map(addOn -> {
                LockedAddOnPrice locked = new LockedAddOnPrice();
                locked.setAddOnId(addOn.getId());
                locked.setAddOnNameAr(addOn.getNameAr());
                locked.setAddOnNameEn(addOn.getNameEn());
                locked.setLockedPrice(addOn.getPrice());
                return locked;
            })
            .toList();
        
        booking.setLockedAddOnPrices(lockedAddOnPrices);
        
        // Calculate total price
        double totalPrice = selectedPackage.getPrice() + 
            addOns.stream().mapToDouble(AddOn::getPrice).sum();
        booking.setTotalPrice(totalPrice);
        
        // Save booking
        booking = bookingRepository.save(booking);
        
        // Generate booking code
        BookingCode bookingCode = bookingCodeService.generateCode(booking);
        booking.setBookingCode(bookingCode);
        
        // Create soft lock on date
        createSoftLock(hall, eventDate);
        
        // Send notification to hall owner
        notificationService.sendNewBookingNotification(booking);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking modifyBooking(Long bookingId, Package newPackage, 
                                  List<AddOn> newAddOns, String reason) {
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Validate modification is allowed
        if (!booking.getCanModify()) {
            throw new RuntimeException("Booking cannot be modified");
        }
        
        if (booking.getModificationCount() >= MAX_MODIFICATIONS) {
            throw new RuntimeException("Maximum modifications reached");
        }
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only modify pending bookings");
        }
        
        // Create modification record
        BookingModification modification = new BookingModification();
        modification.setBooking(booking);
        modification.setModificationNumber(booking.getModificationCount() + 1);
        modification.setReason(reason);
        modification.setModifiedAt(LocalDateTime.now());
        
        // Store previous values
        modification.setPreviousPackageId(booking.getSelectedPackage().getId());
        modification.setPreviousPackagePrice(booking.getLockedPackagePrice());
        
        List<ModificationAddOn> previousAddOns = booking.getLockedAddOnPrices().stream()
            .map(locked -> {
                ModificationAddOn modAddOn = new ModificationAddOn();
                modAddOn.setAddOnId(locked.getAddOnId());
                modAddOn.setNameAr(locked.getAddOnNameAr());
                modAddOn.setNameEn(locked.getAddOnNameEn());
                modAddOn.setPrice(locked.getLockedPrice());
                return modAddOn;
            })
            .toList();
        modification.setPreviousAddOns(previousAddOns);
        modification.setPreviousTotalPrice(booking.getTotalPrice());
        
        // Update booking with new values
        if (newPackage != null) {
            booking.setSelectedPackage(newPackage);
            booking.setLockedPackagePrice(newPackage.getPrice());
            modification.setNewPackageId(newPackage.getId());
            modification.setNewPackagePrice(newPackage.getPrice());
            modification.setType(ModificationType.PACKAGE_CHANGED);
        }
        
        if (newAddOns != null) {
            booking.setSelectedAddOns(newAddOns);
            
            List<LockedAddOnPrice> newLockedPrices = newAddOns.stream()
                .map(addOn -> {
                    LockedAddOnPrice locked = new LockedAddOnPrice();
                    locked.setAddOnId(addOn.getId());
                    locked.setAddOnNameAr(addOn.getNameAr());
                    locked.setAddOnNameEn(addOn.getNameEn());
                    locked.setLockedPrice(addOn.getPrice());
                    return locked;
                })
                .toList();
            booking.setLockedAddOnPrices(newLockedPrices);
            
            List<ModificationAddOn> newModAddOns = newAddOns.stream()
                .map(addOn -> {
                    ModificationAddOn modAddOn = new ModificationAddOn();
                    modAddOn.setAddOnId(addOn.getId());
                    modAddOn.setNameAr(addOn.getNameAr());
                    modAddOn.setNameEn(addOn.getNameEn());
                    modAddOn.setPrice(addOn.getPrice());
                    return modAddOn;
                })
                .toList();
            modification.setNewAddOns(newModAddOns);
            
            if (newPackage != null) {
                modification.setType(ModificationType.PACKAGE_AND_ADDONS_CHANGED);
            } else {
                modification.setType(ModificationType.ADDONS_ADDED);
            }
        }
        
        // Recalculate total price
        double newTotalPrice = booking.getLockedPackagePrice() + 
            booking.getLockedAddOnPrices().stream()
                .mapToDouble(LockedAddOnPrice::getLockedPrice)
                .sum();
        booking.setTotalPrice(newTotalPrice);
        modification.setNewTotalPrice(newTotalPrice);
        
        // Increment modification count
        booking.setModificationCount(booking.getModificationCount() + 1);
        
        // Extend code validity
        BookingCode code = booking.getBookingCode();
        BookingCode extendedCode = bookingCodeService.extendCodeValidity(code.getId());
        modification.setCodeExtendedTo(extendedCode.getExpiresAt());
        
        // Save modification record
        modificationRepository.save(modification);
        
        // Send notification to hall owner
        notificationService.sendBookingModifiedNotification(booking);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking confirmBooking(Long bookingId, HallOwner confirmedBy, 
                                   PaymentMethod paymentMethod, String paymentReference,
                                   boolean scannedOffline) {
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only confirm pending bookings");
        }
        
        // Mark code as used
        BookingCode code = booking.getBookingCode();
        bookingCodeService.markAsUsed(code.getId(), confirmedBy, scannedOffline, null);
        
        // Update booking status
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        booking.setConfirmedBy(confirmedBy);
        booking.setPaymentMethod(paymentMethod);
        booking.setPaymentReference(paymentReference);
        booking.setCanModify(false);
        
        // If offline, mark for sync
        if (scannedOffline) {
            booking.setNeedsSync(true);
        }
        
        // Convert soft lock to hard lock
        convertToHardLock(booking.getHall(), booking.getEventDate());
        
        // Send confirmation notifications
        notificationService.sendBookingConfirmedNotification(booking);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public void handleExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository
            .findExpiredBookings(LocalDateTime.now());
        
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            
            // Remove soft lock
            removeSoftLock(booking.getHall(), booking.getEventDate());
            
            // Invalidate code
            BookingCode code = booking.getBookingCode();
            code.setIsValid(false);
            
            // Send notification
            notificationService.sendBookingExpiredNotification(booking);
        }
        
        bookingRepository.saveAll(expiredBookings);
    }
    
    @Transactional(readOnly = true)
    public boolean isDateAvailable(Long hallId, LocalDate date) {
        // Check if date is blocked
        if (blockedDateRepository.existsByHallIdAndDate(hallId, date)) {
            BlockedDate blocked = blockedDateRepository
                .findByHallIdAndDate(hallId, date).orElse(null);
            
            if (blocked != null && blocked.getBlockType() == BlockType.OWNER_BLOCKED) {
                return false;
            }
        }
        
        // Check if there's a confirmed or pending booking
        List<BookingStatus> blockingStatuses = List.of(
            BookingStatus.CONFIRMED, 
            BookingStatus.PENDING
        );
        
        return !bookingRepository.existsByHallIdAndEventDateAndStatusIn(
            hallId, date, blockingStatuses
        );
    }
    
    private void createSoftLock(Hall hall, LocalDate date) {
        BlockedDate softLock = new BlockedDate();
        softLock.setHall(hall);
        softLock.setDate(date);
        softLock.setBlockType(BlockType.BOOKING_PENDING);
        softLock.setReason("Pending booking confirmation");
        blockedDateRepository.save(softLock);
    }
    
    private void convertToHardLock(Hall hall, LocalDate date) {
        BlockedDate blocked = blockedDateRepository
            .findByHallIdAndDate(hall.getId(), date)
            .orElseThrow(() -> new RuntimeException("Soft lock not found"));
        
        blocked.setBlockType(BlockType.BOOKING_CONFIRMED);
        blocked.setReason("Confirmed booking");
        blockedDateRepository.save(blocked);
    }
    
    private void removeSoftLock(Hall hall, LocalDate date) {
        blockedDateRepository.findByHallIdAndDate(hall.getId(), date)
            .ifPresent(blockedDateRepository::delete);
    }
}