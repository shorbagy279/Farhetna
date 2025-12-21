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
public interface AddOnRepository extends JpaRepository<AddOn, Long> {
    List<AddOn> findByHall(Hall hall);
    List<AddOn> findByHallId(Long hallId);
    List<AddOn> findByHallIdAndActiveTrue(Long hallId);
    List<AddOn> findByCategory(AddOnCategory category);
}
