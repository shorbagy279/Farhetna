package com.farhetna.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String nameAr;
    private String nameEn;
    private String cityAr;
    private String cityEn;
}