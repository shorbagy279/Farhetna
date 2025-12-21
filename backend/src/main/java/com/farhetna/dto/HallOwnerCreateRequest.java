package com.farhetna.dto;

import com.farhetna.model.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HallOwnerCreateRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String businessName;
    private String businessLicense;
    private String taxId;
}