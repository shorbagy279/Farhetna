package com.farhetna.dto;

import com.farhetna.model.PaymentMethod;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmationRequest {
    private String code;
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private Boolean scannedOffline;
}