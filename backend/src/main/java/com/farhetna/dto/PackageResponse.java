package com.farhetna.dto;

import com.farhetna.model.PackageType;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    private Long id;
    private String nameAr;
    private String nameEn;
    private String descriptionAr;
    private String descriptionEn;
    private PackageType type;
    private Double price;
    private List<String> inclusionsAr;
    private List<String> inclusionsEn;
}