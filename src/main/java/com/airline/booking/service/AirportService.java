package com.airline.booking.service;

import com.airline.booking.dto.AirportRequest;
import com.airline.booking.dto.AirportResponse;
import com.airline.booking.entity.Airport;
import com.airline.booking.exception.AppException;
import com.airline.booking.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;

    // Create Airport
    public AirportResponse createAirport(AirportRequest request) {

        if (airportRepository.existsByCode(request.getCode())) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airport code already exists"
            );
        }

        if (airportRepository.existsByName(request.getName())) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airport name already exists"
            );
        }

        Airport airport = Airport.builder()
                .name(request.getName())
                .code(request.getCode())
                .city(request.getCity())
                .country(request.getCountry())
                .build();

        Airport savedAirport = airportRepository.save(airport);

        return mapToResponse(savedAirport);
    }

    // Get All Airports
    public List<AirportResponse> getAllAirports() {

        return airportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Airport By ID
    public AirportResponse getAirportById(Long id) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airport not found"
                ));

        return mapToResponse(airport);
    }

    // Update Airport
    public AirportResponse updateAirport(
            Long id,
            AirportRequest request
    ) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airport not found"
                ));

        if (airportRepository.existsByCodeAndIdNot(
                request.getCode(),
                id
        )) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airport code already exists"
            );
        }

        if (airportRepository.existsByNameAndIdNot(
                request.getName(),
                id
        )) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airport name already exists"
            );
        }

        airport.setName(request.getName());
        airport.setCode(request.getCode());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());
        airport.setUpdatedAt(LocalDateTime.now());

        Airport updatedAirport =
                airportRepository.save(airport);

        return mapToResponse(updatedAirport);
    }

    // Deactivate Airport
    public AirportResponse deactivateAirport(Long id) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airport not found"
                ));

        if (!airport.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airport is already inactive"
            );
        }

        airport.setActive(false);
        airport.setUpdatedAt(LocalDateTime.now());

        Airport updatedAirport =
                airportRepository.save(airport);

        return mapToResponse(updatedAirport);
    }

    // Activate Airport
    public AirportResponse activateAirport(Long id) {

        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airport not found"
                ));

        if (airport.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airport is already active"
            );
        }

        airport.setActive(true);
        airport.setUpdatedAt(LocalDateTime.now());

        Airport updatedAirport =
                airportRepository.save(airport);

        return mapToResponse(updatedAirport);
    }

    @Transactional(readOnly = true)
    public List<AirportResponse> getActiveAirports() {

        return airportRepository
                .findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Entity → Response Mapping
    private AirportResponse mapToResponse(Airport airport) {

        return AirportResponse.builder()
                .id(airport.getId())
                .name(airport.getName())
                .code(airport.getCode())
                .city(airport.getCity())
                .country(airport.getCountry())
                .active(airport.isActive())
                .createdAt(airport.getCreatedAt())
                .updatedAt(airport.getUpdatedAt())
                .build();
    }
}