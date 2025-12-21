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
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByHall(Hall hall);
    List<Rating> findByHallId(Long hallId);
    List<Rating> findByCustomer(Customer customer);
    List<Rating> findByCustomerId(Long customerId);
    Optional<Rating> findByBooking(Booking booking);
    Optional<Rating> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);
    
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.hall.id = :hallId")
    Double calculateAverageRating(@Param("hallId") Long hallId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.hall.id = :hallId")
    Long countRatings(@Param("hallId") Long hallId);
}

