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
public interface HallOwnerRepository extends JpaRepository<HallOwner, Long> {
    Optional<HallOwner> findByUser(User user);
    Optional<HallOwner> findByUserId(Long userId);
    List<HallOwner> findByVerified(Boolean verified);
}