package com.farhetna.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@EqualsAndHashCode(callSuper = true)
public class Booking extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private Package selectedPackage;

    @ManyToMany
    @JoinTable(
        name = "booking_add_ons",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "add_on_id")
    )
    private List<AddOn> selectedAddOns;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private BookingCode bookingCode;

    @Column(nullable = false)
    private Double lockedPackagePrice;

    @ElementCollection
    @CollectionTable(
        name = "booking_locked_addon_prices",
        joinColumns = @JoinColumn(name = "booking_id")
    )
    private List<LockedAddOnPrice> lockedAddOnPrices;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private Integer modificationCount = 0;

    @Column(nullable = false)
    private Boolean canModify = true;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingModification> modifications;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "confirmed_by_owner_id")
    private HallOwner confirmedBy;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String paymentReference;

    @Column(nullable = false)
    private Boolean needsSync = false;

    private LocalDateTime lastSyncedAt;

    @Column(nullable = false)
    private Boolean isExternalBooking = false;

    private String externalCustomerName;
    private String externalCustomerPhone;

    @OneToOne(mappedBy = "booking")
    private Rating rating;

    @Column(nullable = false)
    private Boolean ratingEligible = false;

    private LocalDateTime ratingEligibleAt;
}
