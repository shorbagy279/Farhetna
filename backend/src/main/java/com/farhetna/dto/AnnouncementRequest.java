package com.farhetna.dto;

import com.farhetna.model.*;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementRequest {
    private String titleAr;
    private String titleEn;
    private String contentAr;
    private String contentEn;
    private AnnouncementTarget targetAudience;
    private Boolean sendNotification;
    private AnnouncementPriority priority;
}
