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
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByActiveTrue();
    
    @Query("SELECT a FROM Announcement a WHERE a.active = true " +
           "AND (a.targetAudience = 'ALL_USERS' OR a.targetAudience = :target) " +
           "ORDER BY a.publishedAt DESC")
    List<Announcement> findActiveAnnouncementsForTarget(
        @Param("target") AnnouncementTarget target
    );
}