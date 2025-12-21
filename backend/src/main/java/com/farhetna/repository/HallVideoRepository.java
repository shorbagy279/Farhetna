package com.farhetna.repository;

import com.farhetna.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallVideoRepository extends JpaRepository<HallVideo, Long> {
    List<HallVideo> findByHallId(Long hallId);
}