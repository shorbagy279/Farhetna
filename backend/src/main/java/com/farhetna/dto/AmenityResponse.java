package com.farhetna.dto;

import com.farhetna.model.AmenityCategory;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponse {
    private Long id;
    private String nameAr;
    private String nameEn;
    private String iconUrl;
    private AmenityCategory category;
}
