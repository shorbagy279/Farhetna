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
public interface BookingCodeRepository extends JpaRepository<BookingCode, Long> {
    Optional<BookingCode> findByCode(String code);
    Optional<BookingCode> findByQrCode(String qrCode);
    Optional<BookingCode> findByBooking(Booking booking);
    Optional<BookingCode> findByBookingId(Long bookingId);
    
    @Query("SELECT bc FROM BookingCode bc WHERE bc.code = :code " +
           "AND bc.isValid = true AND bc.isUsed = false " +
           "AND bc.expiresAt > :now")
    Optional<BookingCode> findValidCode(
        @Param("code") String code,
        @Param("now") LocalDateTime now
    );
    
    @Query("SELECT bc FROM BookingCode bc WHERE bc.expiresAt BETWEEN :start AND :end " +
           "AND bc.isValid = true AND bc.isUsed = false")
    List<BookingCode> findCodesExpiringBetween(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}