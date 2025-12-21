package com.farhetna.repository;

import com.farhetna.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<HallPackage, Long> {
    List<HallPackage> findByHall(Hall hall);
    List<HallPackage> findByHallId(Long hallId);
    List<HallPackage> findByHallIdAndActiveTrue(Long hallId);
    List<HallPackage> findByHallAndType(Hall hall, PackageType type);
}
