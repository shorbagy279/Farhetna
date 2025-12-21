package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "halls")
@Data
@EqualsAndHashCode(callSuper = true)
public class Hall extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private HallOwner owner;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HallImage> images;

    @Column(nullable = false)
    private String nameAr;

    @Column(nullable = false)
    private String nameEn;


    @Column(columnDefinition = "TEXT")
    private String descriptionAr;

    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(nullable = false)
    private Integer capacity;
    
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
private List<AddOn> addOns;

@ManyToMany
@JoinTable(
    name = "hall_amenities",
    joinColumns = @JoinColumn(name = "hall_id"),
    inverseJoinColumns = @JoinColumn(name = "amenity_id")
)
private List<Amenity> amenities;


    private String address;
    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean verified = false;

    private Double averageRating = 0.0;
    private Integer totalRatings = 0;
    private Double startingPrice;
}
