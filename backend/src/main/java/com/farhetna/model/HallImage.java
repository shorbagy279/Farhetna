package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hall_images")
@Data
@EqualsAndHashCode(callSuper = true)
public class HallImage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false)
    private String imageUrl;

    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean isPrimary = false;
}
