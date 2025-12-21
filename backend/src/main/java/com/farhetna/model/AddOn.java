package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "add_ons")
@Data
@EqualsAndHashCode(callSuper = true)
public class AddOn extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Column(nullable = false)
    private String nameAr;

    @Column(nullable = false)
    private String nameEn;

    private String descriptionAr;
    private String descriptionEn;

    @Enumerated(EnumType.STRING)
    private AddOnCategory category;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean active = true;

    private String imageUrl;
}
