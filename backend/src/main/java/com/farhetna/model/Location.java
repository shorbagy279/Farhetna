package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "locations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Location extends BaseEntity {

    @Column(nullable = false)
    private String nameAr;

    @Column(nullable = false)
    private String nameEn;

    private String cityAr;
    private String cityEn;

    private String regionAr;
    private String regionEn;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "location")
    private List<Hall> halls;
}
