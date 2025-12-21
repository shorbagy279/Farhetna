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
public interface BlockedDateRepository extends JpaRepository<BlockedDate, Long> {
    List<BlockedDate> findByHall(Hall hall);
    List<BlockedDate> findByHallId(Long hallId);
    
    @Query("SELECT bd FROM BlockedDate bd WHERE bd.hall.id = :hallId " +
           "AND bd.date BETWEEN :startDate AND :endDate")
    List<BlockedDate> findByHallIdAndDateBetween(
        @Param("hallId") Long hallId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    boolean existsByHallIdAndDate(Long hallId, LocalDate date);
    
    Optional<BlockedDate> findByHallIdAndDate(Long hallId, LocalDate date);
}
