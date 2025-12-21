package com.farhetna.mapper;

import com.farhetna.dto.*;
import com.farhetna.model.*;
import org.mapstruct.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {HallMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(disableBuilder = true)
)
public interface BookingMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.user.fullName")
    @Mapping(target = "customerPhone", expression = "java(getCustomerPhone(booking))")
    @Mapping(target = "hallId", source = "hall.id")
    @Mapping(target = "hallName", expression = "java(booking.getHall().getNameEn())")
    @Mapping(target = "selectedPackage", source = "selectedPackage")
    @Mapping(target = "addOns", source = "selectedAddOns")
    @Mapping(target = "bookingCode", source = "bookingCode")
    BookingResponse toResponse(Booking booking);

    @Mapping(
        target = "timeRemainingMinutes",
        expression = "java(calculateTimeRemaining(bookingCode))"
    )
    BookingCodeResponse toCodeResponse(BookingCode bookingCode);

    default String getCustomerPhone(Booking booking) {
        if (booking.getBookingCode() != null && booking.getBookingCode().getIsUsed()) {
            return booking.getCustomer().getUser().getPhoneNumber();
        }
        return null;
    }

    default Long calculateTimeRemaining(BookingCode code) {
        if (code == null || !code.getIsValid()) {
            return 0L;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(code.getExpiresAt())) {
            return 0L;
        }
        return Duration.between(now, code.getExpiresAt()).toMinutes();
    }
}
