package com.farhetna.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.Map;
public enum DateStatus {
    AVAILABLE,     // Green - Can be booked
    PENDING,       // Yellow - Has pending booking
    BOOKED,        // Gray - Confirmed booking
    UNAVAILABLE    // Red - Owner blocked
}