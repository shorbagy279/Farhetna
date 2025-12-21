
package com.farhetna.service;

import com.farhetna.model.*;
import com.farhetna.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final BookingRepository bookingRepository;
    private final HallRepository hallRepository;
    private final CustomerRepository customerRepository;
    private final RatingRepository ratingRepository;
    
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats(Long ownerId) {
        List<Hall> ownerHalls = hallRepository.findByOwnerId(ownerId);
        List<Long> hallIds = ownerHalls.stream().map(Hall::getId).toList();
        
        // Get all bookings for owner's halls
        List<Booking> allBookings = bookingRepository.findByHallIdIn(hallIds);
        
        long totalBookings = allBookings.size();
        long pendingBookings = allBookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.PENDING)
            .count();
        long confirmedBookings = allBookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
            .count();
        long completedBookings = allBookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .count();
        
        double totalRevenue = allBookings.stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || 
                        b.getStatus() == BookingStatus.COMPLETED)
            .mapToDouble(Booking::getTotalPrice)
            .sum();
        
        double averageBookingValue = confirmedBookings > 0 ? 
            totalRevenue / confirmedBookings : 0;
        
        return DashboardStats.builder()
            .totalBookings(totalBookings)
            .pendingBookings(pendingBookings)
            .confirmedBookings(confirmedBookings)
            .completedBookings(completedBookings)
            .totalRevenue(totalRevenue)
            .averageBookingValue(averageBookingValue)
            .totalHalls((long) ownerHalls.size())
            .build();
    }
    
    @Transactional(readOnly = true)
    public List<MonthlyStats> getMonthlyStats(Long ownerId, int year) {
        List<Hall> ownerHalls = hallRepository.findByOwnerId(ownerId);
        List<Long> hallIds = ownerHalls.stream().map(Hall::getId).toList();
        
        List<Booking> yearBookings = bookingRepository.findByHallIdIn(hallIds).stream()
            .filter(b -> b.getCreatedAt().getYear() == year)
            .toList();
        
        Map<Integer, List<Booking>> bookingsByMonth = yearBookings.stream()
            .collect(Collectors.groupingBy(b -> b.getCreatedAt().getMonthValue()));
        
        List<MonthlyStats> stats = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            List<Booking> monthBookings = bookingsByMonth.getOrDefault(month, List.of());
            
            long bookingCount = monthBookings.size();
            double revenue = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || 
                            b.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();
            
            stats.add(MonthlyStats.builder()
                .month(month)
                .year(year)
                .bookingCount(bookingCount)
                .revenue(revenue)
                .build());
        }
        
        return stats;
    }
    
    @Transactional(readOnly = true)
    public List<HallPerformance> getHallPerformance(Long ownerId) {
        List<Hall> ownerHalls = hallRepository.findByOwnerId(ownerId);
        
        return ownerHalls.stream().map(hall -> {
            List<Booking> hallBookings = bookingRepository.findByHallId(hall.getId());
            
            long totalBookings = hallBookings.size();
            long confirmedBookings = hallBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || 
                            b.getStatus() == BookingStatus.COMPLETED)
                .count();
            
            double revenue = hallBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || 
                            b.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();
            
            double conversionRate = totalBookings > 0 ? 
                (confirmedBookings * 100.0) / totalBookings : 0;
            
            return HallPerformance.builder()
                .hallId(hall.getId())
                .hallName(hall.getNameEn())
                .totalBookings(totalBookings)
                .confirmedBookings(confirmedBookings)
                .revenue(revenue)
                .averageRating(hall.getAverageRating())
                .conversionRate(conversionRate)
                .build();
        }).toList();
    }
    
    @Data
    @Builder
    public static class DashboardStats {
        private Long totalBookings;
        private Long pendingBookings;
        private Long confirmedBookings;
        private Long completedBookings;
        private Double totalRevenue;
        private Double averageBookingValue;
        private Long totalHalls;
    }
    
    @Data
    @Builder
    public static class MonthlyStats {
        private Integer month;
        private Integer year;
        private Long bookingCount;
        private Double revenue;
    }
    
    @Data
    @Builder
    public static class HallPerformance {
        private Long hallId;
        private String hallName;
        private Long totalBookings;
        private Long confirmedBookings;
        private Double revenue;
        private Double averageRating;
        private Double conversionRate;
    }
}
