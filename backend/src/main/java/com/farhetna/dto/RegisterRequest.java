package com.farhetna.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;
import com.farhetna.model.*;


@Data
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private Language preferredLanguage;
}




