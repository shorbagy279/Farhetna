package com.farhetna.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private Long hallId;
    private Long packageId;
    private List<Long> addOnIds;
    private LocalDate eventDate;
}