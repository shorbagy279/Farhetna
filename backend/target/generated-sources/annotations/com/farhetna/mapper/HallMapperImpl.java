package com.farhetna.mapper;

import com.farhetna.dto.AddOnResponse;
import com.farhetna.dto.AmenityResponse;
import com.farhetna.dto.HallResponse;
import com.farhetna.dto.LocationResponse;
import com.farhetna.dto.PackageResponse;
import com.farhetna.model.AddOn;
import com.farhetna.model.Amenity;
import com.farhetna.model.Hall;
import com.farhetna.model.HallPackage;
import com.farhetna.model.Location;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-22T00:02:38+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Ubuntu)"
)
@Component
public class HallMapperImpl extends HallMapper {

    @Override
    public HallResponse toResponse(Hall hall, Long userId) {
        if ( hall == null ) {
            return null;
        }

        HallResponse hallResponse = new HallResponse();

        hallResponse.setLocation( toLocationResponse( hall.getLocation() ) );
        hallResponse.setId( hall.getId() );
        hallResponse.setNameAr( hall.getNameAr() );
        hallResponse.setNameEn( hall.getNameEn() );
        hallResponse.setDescriptionAr( hall.getDescriptionAr() );
        hallResponse.setDescriptionEn( hall.getDescriptionEn() );
        hallResponse.setCapacity( hall.getCapacity() );
        hallResponse.setAverageRating( hall.getAverageRating() );
        hallResponse.setTotalRatings( hall.getTotalRatings() );
        hallResponse.setStartingPrice( hall.getStartingPrice() );

        hallResponse.setImageUrls( extractImageUrls(hall) );
        hallResponse.setIsFavorite( checkIfFavorite(hall, userId) );

        return hallResponse;
    }

    @Override
    public PackageResponse toPackageResponse(HallPackage pkg) {
        if ( pkg == null ) {
            return null;
        }

        PackageResponse packageResponse = new PackageResponse();

        packageResponse.setId( pkg.getId() );
        packageResponse.setNameAr( pkg.getNameAr() );
        packageResponse.setNameEn( pkg.getNameEn() );
        packageResponse.setDescriptionAr( pkg.getDescriptionAr() );
        packageResponse.setDescriptionEn( pkg.getDescriptionEn() );
        packageResponse.setType( pkg.getType() );
        packageResponse.setPrice( pkg.getPrice() );

        packageResponse.setInclusionsAr( extractInclusionsAr(pkg) );
        packageResponse.setInclusionsEn( extractInclusionsEn(pkg) );

        return packageResponse;
    }

    @Override
    public AddOnResponse toAddOnResponse(AddOn addOn) {
        if ( addOn == null ) {
            return null;
        }

        AddOnResponse addOnResponse = new AddOnResponse();

        addOnResponse.setId( addOn.getId() );
        addOnResponse.setNameAr( addOn.getNameAr() );
        addOnResponse.setNameEn( addOn.getNameEn() );
        addOnResponse.setDescriptionAr( addOn.getDescriptionAr() );
        addOnResponse.setDescriptionEn( addOn.getDescriptionEn() );
        addOnResponse.setCategory( addOn.getCategory() );
        addOnResponse.setPrice( addOn.getPrice() );
        addOnResponse.setImageUrl( addOn.getImageUrl() );

        return addOnResponse;
    }

    @Override
    public List<AddOnResponse> toAddOnResponses(List<AddOn> addOns) {
        if ( addOns == null ) {
            return null;
        }

        List<AddOnResponse> list = new ArrayList<AddOnResponse>( addOns.size() );
        for ( AddOn addOn : addOns ) {
            list.add( toAddOnResponse( addOn ) );
        }

        return list;
    }

    @Override
    public LocationResponse toLocationResponse(Location location) {
        if ( location == null ) {
            return null;
        }

        LocationResponse locationResponse = new LocationResponse();

        locationResponse.setId( location.getId() );
        locationResponse.setNameAr( location.getNameAr() );
        locationResponse.setNameEn( location.getNameEn() );
        locationResponse.setCityAr( location.getCityAr() );
        locationResponse.setCityEn( location.getCityEn() );

        return locationResponse;
    }

    @Override
    public AmenityResponse toAmenityResponse(Amenity amenity) {
        if ( amenity == null ) {
            return null;
        }

        AmenityResponse amenityResponse = new AmenityResponse();

        amenityResponse.setId( amenity.getId() );
        amenityResponse.setNameAr( amenity.getNameAr() );
        amenityResponse.setNameEn( amenity.getNameEn() );
        amenityResponse.setIconUrl( amenity.getIconUrl() );
        amenityResponse.setCategory( amenity.getCategory() );

        return amenityResponse;
    }
}
