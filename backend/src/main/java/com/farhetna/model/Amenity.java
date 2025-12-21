package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amenities")
@Data
@EqualsAndHashCode(callSuper = true)
public class Amenity extends BaseEntity {

    @Column(nullable = false)
    private String nameAr;

    @Column(nullable = false)
    private String nameEn;

    private String descriptionAr;
    private String descriptionEn;

    private String iconUrl;

    @Enumerated(EnumType.STRING)
    private AmenityCategory category;

    @Column(nullable = false)
    private Boolean active = true;
}
