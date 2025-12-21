package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "blocked_dates")
@Data
@EqualsAndHashCode(callSuper = true)
public class BlockedDate extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false)
    private LocalDate date;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlockType blockType;
}
