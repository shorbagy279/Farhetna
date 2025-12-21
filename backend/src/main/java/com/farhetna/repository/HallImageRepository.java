package com.farhetna.repository;

import com.farhetna.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallImageRepository extends JpaRepository<HallImage, Long> {
    List<HallImage> findByHallId(Long hallId);
    List<HallImage> findByHallIdOrderByDisplayOrderAsc(Long hallId);
    HallImage findByHallIdAndIsPrimaryTrue(Long hallId);
}