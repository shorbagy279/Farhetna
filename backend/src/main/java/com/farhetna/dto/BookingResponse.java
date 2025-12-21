package com.farhetna.dto;

import com.farhetna.model.BookingStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long hallId;
    private String hallName;
    private PackageResponse selectedPackage;
    private List<AddOnResponse> addOns;
    private LocalDate eventDate;
    private BookingStatus status;
    private BookingCodeResponse bookingCode;
    private Double totalPrice;
    private Integer modificationCount;
    private Boolean canModify;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}