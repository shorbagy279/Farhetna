package com.farhetna.repository;

import com.farhetna.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer(Customer customer);
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByHall(Hall hall);
    List<Booking> findByHallId(Long hallId);
    List<Booking> findByStatus(BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId " +
           "AND (:status IS NULL OR b.status = :status) " +
           "ORDER BY b.createdAt DESC")
    List<Booking> findByCustomerIdAndStatus(
        @Param("customerId") Long customerId,
        @Param("status") BookingStatus status
    );
    
    @Query("SELECT b FROM Booking b WHERE b.hall.owner.id = :ownerId " +
           "AND (:status IS NULL OR b.status = :status) " +
           "ORDER BY b.createdAt DESC")
    List<Booking> findByOwnerIdAndStatus(
        @Param("ownerId") Long ownerId,
        @Param("status") BookingStatus status
    );
    
    Optional<Booking> findByHallIdAndEventDate(Long hallId, LocalDate eventDate);
    
    List<Booking> findByHallIdAndEventDateBetween(
        Long hallId, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' " +
           "AND b.bookingCode.expiresAt < :now")
    List<Booking> findExpiredBookings(@Param("now") LocalDateTime now);
    
    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.eventDate < :date AND b.ratingEligible = false")
    List<Booking> findCompletedBookingsForRating(@Param("date") LocalDate date);
    
    @Query("SELECT b FROM Booking b WHERE b.needsSync = true")
    List<Booking> findBookingsNeedingSync();
    @Query("SELECT b FROM Booking b WHERE b.hall.id IN :hallIds")
    List<Booking> findByHallIdIn(@Param("hallIds") List<Long> hallIds);
    
    boolean existsByHallIdAndEventDateAndStatusIn(
        Long hallId, 
        LocalDate eventDate, 
        List<BookingStatus> statuses
    );
}
