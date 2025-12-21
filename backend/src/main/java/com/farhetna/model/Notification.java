package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String titleAr;

    @Column(nullable = false)
    private String titleEn;

    @Column(columnDefinition = "TEXT")
    private String messageAr;

    @Column(columnDefinition = "TEXT")
    private String messageEn;

    @ManyToOne
    @JoinColumn(name = "related_booking_id")
    private Booking relatedBooking;

    @ManyToOne
    @JoinColumn(name = "related_hall_id")
    private Hall relatedHall;

    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime readAt;

    @Column(nullable = false)
    private Boolean sent = false;

    private LocalDateTime sentAt;

    private String deepLink;
}
