package com.farhetna.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;
@Data
@Builder
public class AvailabilityResponse {
    private Long hallId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<LocalDate, DateStatus> availability;
}