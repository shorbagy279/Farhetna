package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "external_bookings")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExternalBooking extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private HallOwner addedBy;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private String customerName;

    private String customerPhone;
    private String notes;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private Boolean blocksDate = true;

    private LocalDateTime addedAt;
}
