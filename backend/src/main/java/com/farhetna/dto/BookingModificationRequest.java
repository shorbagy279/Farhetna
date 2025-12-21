package com.farhetna.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingModificationRequest {
    private Long packageId;
    private List<Long> addOnIds;
    private String reason;
}