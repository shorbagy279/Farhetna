package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "ratings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "hall_id", "booking_id"})
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Rating extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Integer stars;

    @Column(columnDefinition = "TEXT")
    private String reviewAr;

    @Column(columnDefinition = "TEXT")
    private String reviewEn;

    @Column(nullable = false)
    private Boolean isVerified = true;

    private LocalDateTime ratedAt;

    private String ownerResponseAr;
    private String ownerResponseEn;
    private LocalDateTime ownerRespondedAt;
}
