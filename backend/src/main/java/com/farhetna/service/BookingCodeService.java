
package com.farhetna.service;

import com.farhetna.model.*;
import com.farhetna.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class BookingCodeService {
    
    private final BookingCodeRepository bookingCodeRepository;
    private static final int CODE_DISPLAY_LENGTH = 12;
    private static final int CODE_VALIDITY_HOURS = 24;
    private static final int CODE_EXTENSION_HOURS = 2;
    private static final int MAX_EXTENSION_HOURS = 36;
    
    @Transactional
    public BookingCode generateCode(Booking booking) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);
            
            // Create input string
            String input = String.format("%d-%d-%d-%s-%s-%s",
                booking.getHall().getId(),
                booking.getSelectedPackage().getId(),
                booking.getCustomer().getId(),
                booking.getEventDate().toString(),
                System.currentTimeMillis(),
                salt
            );
            
            // Generate SHA-256 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String fullHashedCode = bytesToHex(hash);
            
            // Extract display code (first 12 characters)
            String displayCode = fullHashedCode.substring(0, CODE_DISPLAY_LENGTH).toUpperCase();
            
            // Generate QR code data
            String qrCodeData = String.format("FARHETNA:%s:%d", displayCode, booking.getId());
            
            // Create booking code entity
            BookingCode bookingCode = new BookingCode();
            bookingCode.setBooking(booking);
            bookingCode.setCode(displayCode);
            bookingCode.setFullHashedCode(fullHashedCode);
            bookingCode.setQrCode(qrCodeData);
            bookingCode.setSalt(salt);
            
            LocalDateTime now = LocalDateTime.now();
            bookingCode.setGeneratedAt(now);
            bookingCode.setExpiresAt(now.plusHours(CODE_VALIDITY_HOURS));
            bookingCode.setOriginalExpiryTime(now.plusHours(CODE_VALIDITY_HOURS));
            
            return bookingCodeRepository.save(bookingCode);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate booking code", e);
        }
    }
    
    @Transactional
    public BookingCode extendCodeValidity(Long bookingCodeId) {
        BookingCode code = bookingCodeRepository.findById(bookingCodeId)
            .orElseThrow(() -> new RuntimeException("Booking code not found"));
        
        LocalDateTime newExpiry = code.getExpiresAt().plusHours(CODE_EXTENSION_HOURS);
        LocalDateTime maxExpiry = code.getGeneratedAt().plusHours(MAX_EXTENSION_HOURS);
        
        if (newExpiry.isAfter(maxExpiry)) {
            throw new RuntimeException("Cannot extend beyond maximum validity period");
        }
        
        code.setExpiresAt(newExpiry);
        code.setExtensionCount(code.getExtensionCount() + 1);
        
        return bookingCodeRepository.save(code);
    }
    
    @Transactional(readOnly = true)
    public BookingCode validateCode(String code) {
        LocalDateTime now = LocalDateTime.now();
        return bookingCodeRepository.findValidCode(code, now)
            .orElseThrow(() -> new RuntimeException("Invalid or expired booking code"));
    }
    
    @Transactional
    public BookingCode markAsUsed(Long bookingCodeId, HallOwner scannedBy, 
                                   boolean scannedOffline, String deviceInfo) {
        BookingCode code = bookingCodeRepository.findById(bookingCodeId)
            .orElseThrow(() -> new RuntimeException("Booking code not found"));
        
        code.setIsUsed(true);
        code.setUsedAt(LocalDateTime.now());
        code.setScannedBy(scannedBy);
        code.setScannedAt(LocalDateTime.now());
        code.setScannedOffline(scannedOffline);
        code.setScanDeviceInfo(deviceInfo);
        
        return bookingCodeRepository.save(code);
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}