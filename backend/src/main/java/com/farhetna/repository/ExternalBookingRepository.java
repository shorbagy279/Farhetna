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
public interface ExternalBookingRepository extends JpaRepository<ExternalBooking, Long> {
    List<ExternalBooking> findByHall(Hall hall);
    List<ExternalBooking> findByHallId(Long hallId);
    List<ExternalBooking> findByAddedBy(HallOwner owner);
    
    @Query("SELECT eb FROM ExternalBooking eb WHERE eb.hall.id = :hallId " +
           "AND eb.eventDate BETWEEN :startDate AND :endDate")
    List<ExternalBooking> findByHallIdAndDateRange(
        @Param("hallId") Long hallId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
