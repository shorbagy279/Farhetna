package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "packages")
@Data
@EqualsAndHashCode(callSuper = true)
public class HallPackage extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;
    
    @Column(nullable = false)
    private String nameAr;
    
    @Column(nullable = false)
    private String nameEn;
    
    @Column(columnDefinition = "TEXT")
    private String descriptionAr;
    
    @Column(columnDefinition = "TEXT")
    private String descriptionEn;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageType type;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @ElementCollection
    @CollectionTable(name = "package_inclusions", 
                     joinColumns = @JoinColumn(name = "package_id"))
    private List<PackageInclusion> inclusions;
}