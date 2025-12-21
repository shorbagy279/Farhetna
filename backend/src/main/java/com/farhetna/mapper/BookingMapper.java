package com.farhetna.mapper;

import com.farhetna.dto.*;
import com.farhetna.model.*;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
@Component
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
    
    @Mapping(target = "timeRemainingMinutes", expression = "java(calculateTimeRemaining(bookingCode))")
    BookingCodeResponse toCodeResponse(BookingCode bookingCode);
    
    default String getCustomerPhone(Booking booking) {
        // Only show phone after code is scanned
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

@Mapper(componentModel = "spring")
@Component
public interface HallMapper {
    
    @Mapping(target = "location", source = "location")
    @Mapping(target = "imageUrls", expression = "java(extractImageUrls(hall))")
    @Mapping(target = "amenities", source = "amenities")
    @Mapping(target = "isFavorite", expression = "java(checkIfFavorite(hall, userId))")
    HallResponse toResponse(Hall hall, @Context Long userId);
    
    @Mapping(target = "inclusionsAr", expression = "java(extractInclusionsAr(pkg))")
    @Mapping(target = "inclusionsEn", expression = "java(extractInclusionsEn(pkg))")
    PackageResponse toPackageResponse(Package pkg);
    
    AddOnResponse toAddOnResponse(AddOn addOn);
    
    LocationResponse toLocationResponse(Location location);
    
    AmenityResponse toAmenityResponse(Amenity amenity);
    
    default List<String> extractImageUrls(Hall hall) {
        if (hall.getImages() == null) return List.of();
        return hall.getImages().stream()
            .sorted((a, b) -> {
                if (a.getIsPrimary()) return -1;
                if (b.getIsPrimary()) return 1;
                return a.getDisplayOrder().compareTo(b.getDisplayOrder());
            })
            .map(HallImage::getImageUrl)
            .toList();
    }
    
    default List<String> extractInclusionsAr(Package pkg) {
        if (pkg.getInclusions() == null) return List.of();
        return pkg.getInclusions().stream()
            .map(PackageInclusion::getItemAr)
            .toList();
    }
    
    default List<String> extractInclusionsEn(Package pkg) {
        if (pkg.getInclusions() == null) return List.of();
        return pkg.getInclusions().stream()
            .map(PackageInclusion::getItemEn)
            .toList();
    }
    
    default Boolean checkIfFavorite(Hall hall, Long userId) {
        // This would be injected from repository in real implementation
        return false; // Placeholder
    }
}
