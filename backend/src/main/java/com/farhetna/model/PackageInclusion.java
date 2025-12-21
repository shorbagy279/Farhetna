package com.farhetna.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PackageInclusion {
    private String itemAr;
    private String itemEn;
}
