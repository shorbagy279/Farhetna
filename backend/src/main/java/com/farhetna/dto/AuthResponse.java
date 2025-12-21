package com.farhetna.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;
import com.farhetna.model.*;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private String fullName;
    private UserRole role;
}