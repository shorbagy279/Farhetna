package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "booking_codes",
    indexes = {
        @Index(name = "idx_code", columnList = "code", unique = true),
        @Index(name = "idx_qr_code", columnList = "qrCode", unique = true)
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class BookingCode extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false, unique = true, length = 16)
    private String code;

    @Column(nullable = false, unique = true, length = 64)
    private String fullHashedCode;

    @Column(nullable = false, unique = true)
    private String qrCode;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean isValid = true;

    @Column(nullable = false)
    private Boolean isUsed = false;

    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(name = "scanned_by_owner_id")
    private HallOwner scannedBy;

    private LocalDateTime scannedAt;
    private String scanDeviceInfo;

    @Column(nullable = false)
    private Boolean scannedOffline = false;

    private String salt;
    private Integer version = 1;

    @Column(nullable = false)
    private Integer extensionCount = 0;

    @Column(nullable = false)
    private LocalDateTime originalExpiryTime;
}
