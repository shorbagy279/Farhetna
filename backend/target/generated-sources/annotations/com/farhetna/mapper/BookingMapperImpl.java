package com.farhetna.mapper;

import com.farhetna.dto.BookingCodeResponse;
import com.farhetna.dto.BookingResponse;
import com.farhetna.model.Booking;
import com.farhetna.model.BookingCode;
import com.farhetna.model.Customer;
import com.farhetna.model.Hall;
import com.farhetna.model.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-22T00:10:57+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Autowired
    private HallMapper hallMapper;

    @Override
    public BookingResponse toResponse(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setCustomerId( bookingCustomerId( booking ) );
        bookingResponse.setCustomerName( bookingCustomerUserFullName( booking ) );
        bookingResponse.setHallId( bookingHallId( booking ) );
        bookingResponse.setSelectedPackage( hallMapper.toPackageResponse( booking.getSelectedPackage() ) );
        bookingResponse.setAddOns( hallMapper.toAddOnResponses( booking.getSelectedAddOns() ) );
        bookingResponse.setBookingCode( toCodeResponse( booking.getBookingCode() ) );
        bookingResponse.setId( booking.getId() );
        bookingResponse.setEventDate( booking.getEventDate() );
        bookingResponse.setStatus( booking.getStatus() );
        bookingResponse.setTotalPrice( booking.getTotalPrice() );
        bookingResponse.setModificationCount( booking.getModificationCount() );
        bookingResponse.setCanModify( booking.getCanModify() );
        bookingResponse.setCreatedAt( booking.getCreatedAt() );
        bookingResponse.setConfirmedAt( booking.getConfirmedAt() );

        bookingResponse.setCustomerPhone( getCustomerPhone(booking) );
        bookingResponse.setHallName( booking.getHall().getNameEn() );

        return bookingResponse;
    }

    @Override
    public BookingCodeResponse toCodeResponse(BookingCode bookingCode) {
        if ( bookingCode == null ) {
            return null;
        }

        BookingCodeResponse bookingCodeResponse = new BookingCodeResponse();

        bookingCodeResponse.setCode( bookingCode.getCode() );
        bookingCodeResponse.setQrCode( bookingCode.getQrCode() );
        bookingCodeResponse.setGeneratedAt( bookingCode.getGeneratedAt() );
        bookingCodeResponse.setExpiresAt( bookingCode.getExpiresAt() );
        bookingCodeResponse.setIsValid( bookingCode.getIsValid() );
        bookingCodeResponse.setIsUsed( bookingCode.getIsUsed() );

        bookingCodeResponse.setTimeRemainingMinutes( calculateTimeRemaining(bookingCode) );

        return bookingCodeResponse;
    }

    private Long bookingCustomerId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Customer customer = booking.getCustomer();
        if ( customer == null ) {
            return null;
        }
        Long id = customer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookingCustomerUserFullName(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Customer customer = booking.getCustomer();
        if ( customer == null ) {
            return null;
        }
        User user = customer.getUser();
        if ( user == null ) {
            return null;
        }
        String fullName = user.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }

    private Long bookingHallId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Hall hall = booking.getHall();
        if ( hall == null ) {
            return null;
        }
        Long id = hall.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
