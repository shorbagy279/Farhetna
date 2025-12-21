package com.farhetna.service;

import com.farhetna.dto.*;
import com.farhetna.model.*;
import com.farhetna.repository.*;
import com.farhetna.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepository userRepository;
    private final HallOwnerRepository hallOwnerRepository;
    private final HallRepository hallRepository;
    private final BookingRepository bookingRepository;
    private final AnnouncementRepository announcementRepository;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Transactional
    public AuthResponse createHallOwner(HallOwnerCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(UserRole.HALL_OWNER);
        user.setPreferredLanguage(Language.ARABIC);
        user = userRepository.save(user);
        
        // Create hall owner profile
        HallOwner hallOwner = new HallOwner();
        hallOwner.setUser(user);
        hallOwner.setBusinessName(request.getBusinessName());
        hallOwner.setBusinessLicense(request.getBusinessLicense());
        hallOwner.setTaxId(request.getTaxId());
        hallOwner.setVerified(false);
        hallOwnerRepository.save(hallOwner);
        
        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .role(user.getRole())
            .build();
    }
    
    @Transactional
    public HallResponse createHall(HallCreateRequest request) {
        HallOwner owner = hallOwnerRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new RuntimeException("Hall owner not found"));
        
        Location location = locationRepository.findById(request.getLocationId())
            .orElseThrow(() -> new RuntimeException("Location not found"));
        
        Hall hall = new Hall();
        hall.setOwner(owner);
        hall.setLocation(location);
        hall.setNameAr(request.getNameAr());
        hall.setNameEn(request.getNameEn());
        hall.setDescriptionAr(request.getDescriptionAr());
        hall.setDescriptionEn(request.getDescriptionEn());
        hall.setCapacity(request.getCapacity());
        hall.setAddress(request.getAddress());
        hall.setActive(true);
        hall.setVerified(false);
        
        hall = hallRepository.save(hall);
        
        // Convert to response (would use mapper in real implementation)
        return HallResponse.builder()
            .id(hall.getId())
            .nameAr(hall.getNameAr())
            .nameEn(hall.getNameEn())
            .build();
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings(String status) {
        List<Booking> bookings;
        if (status != null) {
            bookings = bookingRepository.findByStatus(BookingStatus.valueOf(status));
        } else {
            bookings = bookingRepository.findAll();
        }
        
        // Would use mapper in real implementation
        return bookings.stream()
            .map(this::toBookingResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void createAnnouncement(Long adminId, AnnouncementRequest request) {
        Administrator admin = administratorRepository.findByUserId(adminId)
            .orElseThrow(() -> new RuntimeException("Administrator not found"));
        
        Announcement announcement = new Announcement();
        announcement.setCreatedBy(admin);
        announcement.setTitleAr(request.getTitleAr());
        announcement.setTitleEn(request.getTitleEn());
        announcement.setContentAr(request.getContentAr());
        announcement.setContentEn(request.getContentEn());
        announcement.setTargetAudience(request.getTargetAudience());
        announcement.setActive(true);
        announcement.setPublishedAt(LocalDateTime.now());
        announcement.setSendNotification(request.getSendNotification());
        announcement.setPriority(request.getPriority());
        
        announcementRepository.save(announcement);
    }
    
    private BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
            .id(booking.getId())
            .customerId(booking.getCustomer().getId())
            .customerName(booking.getCustomer().getUser().getFullName())
            .hallId(booking.getHall().getId())
            .hallName(booking.getHall().getNameEn())
            .eventDate(booking.getEventDate())
            .status(booking.getStatus())
            .totalPrice(booking.getTotalPrice())
            .build();
    }
    
    private final LocationRepository locationRepository;
}
