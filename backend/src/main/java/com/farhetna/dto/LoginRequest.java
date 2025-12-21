package com.farhetna.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class LoginRequest {
    private String email;
    private String password;
}