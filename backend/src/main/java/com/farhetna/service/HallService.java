package com.farhetna.service;

import com.farhetna.dto.*;
import com.farhetna.mapper.HallMapper;
import com.farhetna.model.*;
import com.farhetna.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HallService {
    
    private final HallRepository hallRepository;
    private final PackageRepository packageRepository;
    private final AddOnRepository addOnRepository;
    private final FavoriteRepository favoriteRepository;
    private final CustomerRepository customerRepository;
    private final BlockedDateRepository blockedDateRepository;
    private final BookingRepository bookingRepository;
    private final HallMapper hallMapper;
    
    @Transactional(readOnly = true)
    public List<HallResponse> searchHalls(HallSearchRequest request, Long userId) {
        List<Hall> halls = hallRepository.searchHalls(
            request.getLocationId(),
            request.getMinCapacity(),
            request.getMaxPrice(),
            request.getMinRating()
        );
        
        // Filter by amenities if specified
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            halls = hallRepository.findByAllAmenitiesIn(
                request.getAmenityIds(),
                (long) request.getAmenityIds().size()
            );
        }
        
        // Filter by availability if date is specified
        if (request.getCheckDate() != null) {
            halls = halls.stream()
                .filter(hall -> isDateAvailable(hall.getId(), request.getCheckDate()))
                .collect(Collectors.toList());
        }
        
        return halls.stream()
            .map(hall -> hallMapper.toResponse(hall, userId))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public HallResponse getHallDetails(Long hallId, Long userId) {
        Hall hall = hallRepository.findById(hallId)
            .orElseThrow(() -> new RuntimeException("Hall not found"));
        
        return hallMapper.toResponse(hall, userId);
    }
    
    @Transactional(readOnly = true)
    public Hall getHallById(Long hallId) {
        return hallRepository.findById(hallId)
            .orElseThrow(() -> new RuntimeException("Hall not found"));
    }
    
    @Transactional(readOnly = true)
    public List<PackageResponse> getHallPackages(Long hallId) {
        List<HallPackage> packages = packageRepository.findByHallIdAndActiveTrue(hallId);
        return packages.stream()
            .map(hallMapper::toPackageResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AddOnResponse> getHallAddOns(Long hallId) {
        List<AddOn> addOns = addOnRepository.findByHallIdAndActiveTrue(hallId);
        return addOns.stream()
            .map(hallMapper::toAddOnResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailability(Long hallId, LocalDate startDate, LocalDate endDate) {
        List<BlockedDate> blockedDates = blockedDateRepository
            .findByHallIdAndDateBetween(hallId, startDate, endDate);
        
        List<Booking> bookings = bookingRepository
            .findByHallIdAndEventDateBetween(hallId, startDate, endDate);
        
        Map<LocalDate, DateStatus> availability = new HashMap<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            DateStatus status = determineDateStatus(currentDate, blockedDates, bookings);
            availability.put(currentDate, status);
            currentDate = currentDate.plusDays(1);
        }
        
        return AvailabilityResponse.builder()
            .hallId(hallId)
            .startDate(startDate)
            .endDate(endDate)
            .availability(availability)
            .build();
    }
    
    @Transactional
    public void toggleFavorite(Long userId, Long hallId) {
        Customer customer = customerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Optional<Favorite> existing = favoriteRepository
            .findByCustomerIdAndHallId(customer.getId(), hallId);
        
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
        } else {
            Hall hall = getHallById(hallId);
            Favorite favorite = new Favorite();
            favorite.setCustomer(customer);
            favorite.setHall(hall);
            favorite.setFavoritedAt(LocalDateTime.now());
            favoriteRepository.save(favorite);
        }
    }
    
    @Transactional(readOnly = true)
    public List<HallResponse> getFavoriteHalls(Long userId) {
        Customer customer = customerRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return favoriteRepository.findByCustomerId(customer.getId()).stream()
            .map(Favorite::getHall)
            .map(hall -> hallMapper.toResponse(hall, userId))
            .collect(Collectors.toList());
    }
    
    private boolean isDateAvailable(Long hallId, LocalDate date) {
        // Check blocked dates
        if (blockedDateRepository.existsByHallIdAndDate(hallId, date)) {
            return false;
        }
        
        // Check existing bookings
        List<BookingStatus> blockingStatuses = List.of(
            BookingStatus.CONFIRMED,
            BookingStatus.PENDING
        );
        
        return !bookingRepository.existsByHallIdAndEventDateAndStatusIn(
            hallId, date, blockingStatuses
        );
    }
    
    private DateStatus determineDateStatus(LocalDate date, List<BlockedDate> blockedDates, 
                                          List<Booking> bookings) {
        // Check if date is blocked by owner
        boolean ownerBlocked = blockedDates.stream()
            .anyMatch(bd -> bd.getDate().equals(date) && 
                           bd.getBlockType() == BlockType.OWNER_BLOCKED);
        if (ownerBlocked) return DateStatus.UNAVAILABLE;
        
        // Check if date has confirmed booking
        boolean hasConfirmed = bookings.stream()
            .anyMatch(b -> b.getEventDate().equals(date) && 
                          b.getStatus() == BookingStatus.CONFIRMED);
        if (hasConfirmed) return DateStatus.BOOKED;
        
        // Check if date has pending booking
        boolean hasPending = bookings.stream()
            .anyMatch(b -> b.getEventDate().equals(date) && 
                          b.getStatus() == BookingStatus.PENDING);
        if (hasPending) return DateStatus.PENDING;
        
        return DateStatus.AVAILABLE;
    }
}
