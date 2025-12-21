package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
@Data
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "created_by_admin_id", nullable = false)
    private Administrator createdBy;

    @Column(nullable = false)
    private String titleAr;

    @Column(nullable = false)
    private String titleEn;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contentAr;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contentEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementTarget targetAudience;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private Boolean sendNotification = false;

    @Enumerated(EnumType.STRING)
    private AnnouncementPriority priority;
}
