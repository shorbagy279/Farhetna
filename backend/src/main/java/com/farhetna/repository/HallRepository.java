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
public interface HallRepository extends JpaRepository<Hall, Long> {
    List<Hall> findByActiveTrue();
    List<Hall> findByOwner(HallOwner owner);
    List<Hall> findByOwnerId(Long ownerId);
    List<Hall> findByLocation(Location location);
    List<Hall> findByLocationId(Long locationId);
    
    @Query("SELECT h FROM Hall h WHERE h.active = true AND h.verified = true " +
           "AND (:locationId IS NULL OR h.location.id = :locationId) " +
           "AND (:minCapacity IS NULL OR h.capacity >= :minCapacity) " +
           "AND (:maxPrice IS NULL OR h.startingPrice <= :maxPrice) " +
           "AND (:minRating IS NULL OR h.averageRating >= :minRating)")
    List<Hall> searchHalls(
        @Param("locationId") Long locationId,
        @Param("minCapacity") Integer minCapacity,
        @Param("maxPrice") Double maxPrice,
        @Param("minRating") Double minRating
    );
    
    @Query("SELECT h FROM Hall h JOIN h.amenities a WHERE a.id IN :amenityIds " +
       "GROUP BY h.id HAVING COUNT(DISTINCT a.id) = :amenityCount")
List<Hall> findByAllAmenitiesIn(
    @Param("amenityIds") List<Long> amenityIds,
    @Param("amenityCount") Long amenityCount
);

@Query("SELECT h FROM Hall h JOIN h.addOns a WHERE a.id IN :addOnIds " +
       "GROUP BY h.id HAVING COUNT(DISTINCT a.id) = :addOnCount")
List<Hall> findByAllAddOnsIn(
    @Param("addOnIds") List<Long> addOnIds,
    @Param("addOnCount") Long addOnCount
);

}


