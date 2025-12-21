package com.farhetna.service;

import com.farhetna.dto.*;
import com.farhetna.mapper.BookingMapper;
import com.farhetna.model.*;
import com.farhetna.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final BookingCodeService bookingCodeService;
    private final BookingModificationRepository modificationRepository;
    private final BlockedDateRepository blockedDateRepository;
    private final NotificationService notificationService;
    private final CustomerRepository customerRepository;
    private final HallRepository hallRepository;
    private final PackageRepository packageRepository;
    private final AddOnRepository addOnRepository;
    private final HallOwnerRepository hallOwnerRepository;
    private final BookingMapper bookingMapper;
    
    private static final int MAX_MODIFICATIONS = 3;
    
    // ========== API Methods ==========
    
    @Transactional
    public BookingResponse createBookingFromRequest(Long userId, BookingRequest request) {
        Customer customer = customerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Hall hall = hallRepository.findById(request.getHallId())
            .orElseThrow(() -> new RuntimeException("Hall not found"));
        
        HallPackage selectedPackage = packageRepository.findById(request.getPackageId())
            .orElseThrow(() -> new RuntimeException("Package not found"));
        
        List<AddOn> addOns = addOnRepository.findAllById(request.getAddOnIds());
        
        Booking booking = createBooking(customer, hall, selectedPackage, addOns, request.getEventDate());
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional
    public BookingResponse modifyBookingFromRequest(Long bookingId, Long userId, 
                                                    BookingModificationRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getCustomer().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        HallPackage newPackage = request.getPackageId() != null ?
            packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found")) : null;
        
        List<AddOn> newAddOns = request.getAddOnIds() != null ?
            addOnRepository.findAllById(request.getAddOnIds()) : null;
        
        booking = modifyBooking(bookingId, newPackage, newAddOns, request.getReason());
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional
    public BookingResponse confirmBookingByOwner(Long bookingId, Long ownerId, 
                                                  BookingConfirmationRequest request) {
        HallOwner owner = hallOwnerRepository.findByUserId(ownerId)
            .orElseThrow(() -> new RuntimeException("Hall owner not found"));
        
        Booking booking = confirmBooking(bookingId, owner, 
            request.getPaymentMethod(), 
            request.getPaymentReference(),
            request.getScannedOffline() != null ? request.getScannedOffline() : false);
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional
    public BookingResponse validateBookingCode(String code, Long ownerId) {
        BookingCode bookingCode = bookingCodeService.validateCode(code);
        Booking booking = bookingCode.getBooking();
        
        if (!booking.getHall().getOwner().getUser().getId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        return bookingMapper.toResponse(booking);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getCustomerBookings(Long userId, BookingStatus status) {
        Customer customer = customerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        List<Booking> bookings = bookingRepository.findByCustomerIdAndStatus(
            customer.getId(), status);
        
        return bookings.stream()
            .map(bookingMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getOwnerBookings(Long ownerId, BookingStatus status) {
        HallOwner owner = hallOwnerRepository.findByUserId(ownerId)
            .orElseThrow(() -> new RuntimeException("Hall owner not found"));
        
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(
            owner.getId(), status);
        
        return bookings.stream()
            .map(bookingMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public List<BookingResponse> syncOfflineBookings(Long ownerId, 
                                                      List<BookingConfirmationRequest> requests) {
        return requests.stream()
            .map(request -> confirmBookingByOwner(
                extractBookingIdFromCode(request.getCode()), 
                ownerId, 
                request))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getPendingSyncBookings(Long ownerId) {
        List<Booking> bookings = bookingRepository.findBookingsNeedingSync();
        
        return bookings.stream()
            .filter(b -> b.getHall().getOwner().getUser().getId().equals(ownerId))
            .map(bookingMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    // ========== Core Booking Logic ==========
    
    @Transactional
    public Booking createBooking(Customer customer, Hall hall, HallPackage selectedPackage,
                                  List<AddOn> addOns, LocalDate eventDate) {
        
        if (!isDateAvailable(hall.getId(), eventDate)) {
            throw new RuntimeException("Selected date is not available");
        }
        
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHall(hall);
        booking.setSelectedPackage(selectedPackage);
        booking.setSelectedAddOns(addOns);
        booking.setEventDate(eventDate);
        booking.setStatus(BookingStatus.PENDING);
        
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
            .collect(Collectors.toList());
        
        booking.setLockedAddOnPrices(lockedAddOnPrices);
        
        double totalPrice = selectedPackage.getPrice() + 
            addOns.stream().mapToDouble(AddOn::getPrice).sum();
        booking.setTotalPrice(totalPrice);
        
        booking = bookingRepository.save(booking);
        
        BookingCode bookingCode = bookingCodeService.generateCode(booking);
        booking.setBookingCode(bookingCode);
        
        createSoftLock(hall, eventDate);
        
        notificationService.sendNewBookingNotification(booking);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking modifyBooking(Long bookingId, HallPackage newPackage, 
                                  List<AddOn> newAddOns, String reason) {
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getCanModify()) {
            throw new RuntimeException("Booking cannot be modified");
        }
        
        if (booking.getModificationCount() >= MAX_MODIFICATIONS) {
            throw new RuntimeException("Maximum modifications reached");
        }
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Can only modify pending bookings");
        }
        
        BookingModification modification = new BookingModification();
        modification.setBooking(booking);
        modification.setModificationNumber(booking.getModificationCount() + 1);
        modification.setReason(reason);
        modification.setModifiedAt(LocalDateTime.now());
        
        modification.setPreviousPackageId(booking.getSelectedPackage().getId());
        modification.setPreviousPackagePrice(booking.getLockedPackagePrice());
        modification.setPreviousTotalPrice(booking.getTotalPrice());
        
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
                .collect(Collectors.toList());
            booking.setLockedAddOnPrices(newLockedPrices);
            
            if (newPackage != null) {
                modification.setType(ModificationType.PACKAGE_AND_ADDONS_CHANGED);
            } else {
                modification.setType(ModificationType.ADDONS_ADDED);
            }
        }
        
        double newTotalPrice = booking.getLockedPackagePrice() + 
            booking.getLockedAddOnPrices().stream()
                .mapToDouble(LockedAddOnPrice::getLockedPrice)
                .sum();
        booking.setTotalPrice(newTotalPrice);
        modification.setNewTotalPrice(newTotalPrice);
        
        booking.setModificationCount(booking.getModificationCount() + 1);
        
        BookingCode code = booking.getBookingCode();
        BookingCode extendedCode = bookingCodeService.extendCodeValidity(code.getId());
        modification.setCodeExtendedTo(extendedCode.getExpiresAt());
        
        modificationRepository.save(modification);
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
        
        BookingCode code = booking.getBookingCode();
        bookingCodeService.markAsUsed(code.getId(), confirmedBy, scannedOffline, null);
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        booking.setConfirmedBy(confirmedBy);
        booking.setPaymentMethod(paymentMethod);
        booking.setPaymentReference(paymentReference);
        booking.setCanModify(false);
        
        if (scannedOffline) {
            booking.setNeedsSync(true);
        }
        
        convertToHardLock(booking.getHall(), booking.getEventDate());
        notificationService.sendBookingConfirmedNotification(booking);
        
        return bookingRepository.save(booking);
    }
    
    @Transactional
    public void handleExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository
            .findExpiredBookings(LocalDateTime.now());
        
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            removeSoftLock(booking.getHall(), booking.getEventDate());
            
            BookingCode code = booking.getBookingCode();
            code.setIsValid(false);
            
            notificationService.sendBookingExpiredNotification(booking);
        }
        
        bookingRepository.saveAll(expiredBookings);
    }
    
    @Transactional
    public void updateRatingEligibility() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Booking> completedBookings = bookingRepository
            .findCompletedBookingsForRating(yesterday);
        
        for (Booking booking : completedBookings) {
            booking.setRatingEligible(true);
            booking.setRatingEligibleAt(LocalDateTime.now());
            notificationService.sendRatingRequestNotification(booking);
        }
        
        bookingRepository.saveAll(completedBookings);
    }
    
    @Transactional
    public void syncOfflineBookings() {
        List<Booking> needsSync = bookingRepository.findBookingsNeedingSync();
        
        for (Booking booking : needsSync) {
            booking.setNeedsSync(false);
            booking.setLastSyncedAt(LocalDateTime.now());
        }
        
        bookingRepository.saveAll(needsSync);
    }
    
    @Transactional(readOnly = true)
    public boolean isDateAvailable(Long hallId, LocalDate date) {
        if (blockedDateRepository.existsByHallIdAndDate(hallId, date)) {
            BlockedDate blocked = blockedDateRepository
                .findByHallIdAndDate(hallId, date).orElse(null);
            
            if (blocked != null && blocked.getBlockType() == BlockType.OWNER_BLOCKED) {
                return false;
            }
        }
        
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
    
    private Long extractBookingIdFromCode(String code) {
        BookingCode bookingCode = bookingCodeService.validateCode(code);
        return bookingCode.getBooking().getId();
    }
}