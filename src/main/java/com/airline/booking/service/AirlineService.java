package com.airline.booking.service;

import com.airline.booking.dto.AirlineRequest;
import com.airline.booking.dto.AirlineResponse;
import com.airline.booking.entity.Airline;
import com.airline.booking.exception.AppException;
import com.airline.booking.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;

    public AirlineResponse createAirline(AirlineRequest request) {

        if (airlineRepository.existsByCode(request.getCode())) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airline code already exists"
            );
        }

        if (airlineRepository.existsByName(request.getName())) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airline name already exists"
            );
        }

        Airline airline = Airline.builder()
                .name(request.getName())
                .code(request.getCode())
                .build();

        Airline savedAirline = airlineRepository.save(airline);

        return mapToResponse(savedAirline);
    }

    private AirlineResponse mapToResponse(Airline airline) {

        return AirlineResponse.builder()
                .id(airline.getId())
                .name(airline.getName())
                .code(airline.getCode())
                .active(airline.isActive())
                .createdAt(airline.getCreatedAt())
                .updatedAt(airline.getUpdatedAt())
                .build();
    }

    public List<AirlineResponse> getAllAirlines() {

        return airlineRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AirlineResponse getAirlineById(Long id) {

        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        return mapToResponse(airline);
    }

    public AirlineResponse updateAirline(
            Long id,
            AirlineRequest request
    ) {

        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        if (airlineRepository.existsByCodeAndIdNot(
                request.getCode(),
                id
        )) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airline code already exists"
            );
        }

        if (airlineRepository.existsByNameAndIdNot(
                request.getName(),
                id
        )) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airline name already exists"
            );
        }

        airline.setName(request.getName());
        airline.setCode(request.getCode());
        airline.setUpdatedAt(LocalDateTime.now());

        Airline updatedAirline =
                airlineRepository.save(airline);

        return mapToResponse(updatedAirline);
    }

    public AirlineResponse deactivateAirline(Long id) {

        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        if (!airline.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airline is already inactive"
            );
        }

        airline.setActive(false);
        airline.setUpdatedAt(LocalDateTime.now());

        Airline updatedAirline =
                airlineRepository.save(airline);

        return mapToResponse(updatedAirline);
    }

    public AirlineResponse activateAirline(Long id) {

        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        if (airline.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Airline is already active"
            );
        }

        airline.setActive(true);
        airline.setUpdatedAt(LocalDateTime.now());

        Airline updatedAirline =
                airlineRepository.save(airline);

        return mapToResponse(updatedAirline);
    }
}