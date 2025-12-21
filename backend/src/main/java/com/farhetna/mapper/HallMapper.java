package com.farhetna.mapper;

import com.farhetna.dto.*;
import com.farhetna.model.*;
import com.farhetna.repository.FavoriteRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class HallMapper {

    @Autowired
    protected FavoriteRepository favoriteRepository;

    // ================= HALL =================
    @Mapping(target = "location", source = "location")
    @Mapping(target = "imageUrls", expression = "java(extractImageUrls(hall))")
    @Mapping(target = "isFavorite", expression = "java(checkIfFavorite(hall, userId))")
    @Mapping(target = "addOns", expression = "java(toAddOnResponses(hall.getAddOns()))")
    @Mapping(target = "amenities", expression = "java(toAmenityResponses(hall.getAmenities()))")
    public abstract HallResponse toResponse(Hall hall, @Context Long userId);

    // ================= PACKAGE =================
    @Mapping(target = "inclusionsAr", expression = "java(extractInclusionsAr(pkg))")
    @Mapping(target = "inclusionsEn", expression = "java(extractInclusionsEn(pkg))")
    public abstract PackageResponse toPackageResponse(HallPackage pkg);

    public List<PackageResponse> toPackageResponses(List<HallPackage> packages) {
        if (packages == null) return List.of();
        return packages.stream()
                       .map(this::toPackageResponse)
                       .collect(Collectors.toList());
    }

    // ================= ADD-ON =================
    public abstract AddOnResponse toAddOnResponse(AddOn addOn);

    public List<AddOnResponse> toAddOnResponses(List<AddOn> addOns) {
        if (addOns == null) return List.of();
        return addOns.stream()
                     .map(this::toAddOnResponse)
                     .collect(Collectors.toList());
    }

    // ================= AMENITIES =================
    public abstract AmenityResponse toAmenityResponse(Amenity amenity);

    public List<AmenityResponse> toAmenityResponses(List<Amenity> amenities) {
        if (amenities == null) return List.of();
        return amenities.stream()
                        .map(this::toAmenityResponse)
                        .collect(Collectors.toList());
    }

    // ================= HELPERS =================
    protected List<String> extractImageUrls(Hall hall) {
        if (hall.getImages() == null) return List.of();
        return hall.getImages().stream()
                   .sorted((a, b) -> {
                       if (Boolean.TRUE.equals(a.getIsPrimary())) return -1;
                       if (Boolean.TRUE.equals(b.getIsPrimary())) return 1;
                       return a.getDisplayOrder().compareTo(b.getDisplayOrder());
                   })
                   .map(HallImage::getImageUrl)
                   .collect(Collectors.toList());
    }

    protected List<String> extractInclusionsAr(HallPackage pkg) {
        if (pkg.getInclusions() == null) return List.of();
        return pkg.getInclusions().stream()
                  .map(PackageInclusion::getItemAr)
                  .collect(Collectors.toList());
    }

    protected List<String> extractInclusionsEn(HallPackage pkg) {
        if (pkg.getInclusions() == null) return List.of();
        return pkg.getInclusions().stream()
                  .map(PackageInclusion::getItemEn)
                  .collect(Collectors.toList());
    }

    protected Boolean checkIfFavorite(Hall hall, Long userId) {
        if (userId == null) return false;
        return favoriteRepository.existsByCustomerIdAndHallId(userId, hall.getId());
    }
}
