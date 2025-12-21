package com.farhetna.service;

import com.farhetna.dto.LocationResponse;
import com.farhetna.model.Location;
import com.farhetna.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    
    private final LocationRepository locationRepository;
    
    @Transactional(readOnly = true)
    public List<LocationResponse> getAllActiveLocations() {
        return locationRepository.findByActiveTrue().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    private LocationResponse toResponse(Location location) {
        return LocationResponse.builder()
            .id(location.getId())
            .nameAr(location.getNameAr())
            .nameEn(location.getNameEn())
            .cityAr(location.getCityAr())
            .cityEn(location.getCityEn())
            .build();
    }
}
