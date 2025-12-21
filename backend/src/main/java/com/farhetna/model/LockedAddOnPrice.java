package com.farhetna.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class LockedAddOnPrice {
    private Long addOnId;
    private String addOnNameAr;
    private String addOnNameEn;
    private Double lockedPrice;
}
