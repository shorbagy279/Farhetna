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
public interface HallPackageRepository extends JpaRepository<Package, Long> {
    List<HallPackage> findByHall(Hall hall);
    List<HallPackage> findByHallId(Long hallId);
    List<HallPackage> findByHallIdAndActiveTrue(Long hallId);
    List<HallPackage> findByHallAndType(Hall hall, PackageType type);
}

