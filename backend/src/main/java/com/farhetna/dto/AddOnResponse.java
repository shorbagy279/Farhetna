package com.farhetna.dto;

import com.farhetna.model.AddOnCategory;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOnResponse {
    private Long id;
    private String nameAr;
    private String nameEn;
    private String descriptionAr;
    private String descriptionEn;
    private AddOnCategory category;
    private Double price;
    private String imageUrl;
}