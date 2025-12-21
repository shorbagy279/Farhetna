package com.farhetna.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HallSearchRequest {
    private Long locationId;
    private Integer minCapacity;
    private Double maxPrice;
    private Double minRating;
    private List<Long> amenityIds;
    private LocalDate checkDate;
}