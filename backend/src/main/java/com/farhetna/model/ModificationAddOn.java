package com.farhetna.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ModificationAddOn {
    private Long addOnId;
    private String nameAr;
    private String nameEn;
    private Double price;
}
