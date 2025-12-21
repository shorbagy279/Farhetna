package com.farhetna.dto;

import com.farhetna.model.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HallCreateRequest {
    private Long ownerId;
    private Long locationId;
    private String nameAr;
    private String nameEn;
    private String descriptionAr;
    private String descriptionEn;
    private Integer capacity;
    private String address;
}

