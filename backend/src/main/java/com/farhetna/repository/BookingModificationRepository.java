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
public interface BookingModificationRepository extends JpaRepository<BookingModification, Long> {
    List<BookingModification> findByBooking(Booking booking);
    List<BookingModification> findByBookingIdOrderByModificationNumberAsc(Long bookingId);
}
