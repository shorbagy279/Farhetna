package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hall_owners")
@Data
@EqualsAndHashCode(callSuper = true)
public class HallOwner extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Hall> halls;

    private String businessName;
    private String businessLicense;
    private String taxId;

    @Column(nullable = false)
    private Boolean verified = false;

    private LocalDateTime verifiedAt;
}
