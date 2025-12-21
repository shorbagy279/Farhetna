package com.farhetna.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCodeResponse {
    private String code;
    private String qrCode;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private Boolean isValid;
    private Boolean isUsed;
    private Long timeRemainingMinutes;
}
