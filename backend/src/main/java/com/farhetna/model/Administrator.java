package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "administrators")
@Data
@EqualsAndHashCode(callSuper = true)
public class Administrator extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private AdminRole adminRole;

    private LocalDateTime lastLoginAt;
}
