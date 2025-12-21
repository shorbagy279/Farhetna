package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "booking_modifications")
@Data
@EqualsAndHashCode(callSuper = true)
public class BookingModification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Integer modificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModificationType type;

    private Long previousPackageId;
    private Double previousPackagePrice;

    @ElementCollection
    @CollectionTable(
        name = "modification_previous_addons",
        joinColumns = @JoinColumn(name = "modification_id")
    )
    private List<ModificationAddOn> previousAddOns;

    private Double previousTotalPrice;

    private Long newPackageId;
    private Double newPackagePrice;

    @ElementCollection
    @CollectionTable(
        name = "modification_new_addons",
        joinColumns = @JoinColumn(name = "modification_id")
    )
    private List<ModificationAddOn> newAddOns;

    private Double newTotalPrice;
    private LocalDateTime modifiedAt;
    private String reason;

    @Column(nullable = false)
    private LocalDateTime codeExtendedTo;
}
