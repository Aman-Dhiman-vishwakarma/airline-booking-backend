package com.airline.booking.controller.admin;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.AirportRequest;
import com.airline.booking.dto.AirportResponse;
import com.airline.booking.service.AirportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/airports")
@RequiredArgsConstructor
public class AdminAirportController {

    private final AirportService airportService;

    // Create Airport
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AirportResponse> createAirport(
            @Valid @RequestBody AirportRequest request
    ) {

        AirportResponse airportResponse =
                airportService.createAirport(request);

        return new ApiResponse<>(
                true,
                "Airport created successfully",
                airportResponse
        );
    }

    // Get All Airports
    @GetMapping
    public ApiResponse<List<AirportResponse>> getAllAirports() {

        List<AirportResponse> airports =
                airportService.getAllAirports();

        return new ApiResponse<>(
                true,
                "Airports fetched successfully",
                airports
        );
    }

    // Get Airport By ID
    @GetMapping("/{id}")
    public ApiResponse<AirportResponse> getAirportById(
            @PathVariable Long id
    ) {

        AirportResponse airportResponse =
                airportService.getAirportById(id);

        return new ApiResponse<>(
                true,
                "Airport fetched successfully",
                airportResponse
        );
    }

    // Update Airport
    @PutMapping("/{id}")
    public ApiResponse<AirportResponse> updateAirport(
            @PathVariable Long id,
            @Valid @RequestBody AirportRequest request
    ) {

        AirportResponse airportResponse =
                airportService.updateAirport(id, request);

        return new ApiResponse<>(
                true,
                "Airport updated successfully",
                airportResponse
        );
    }

    // Deactivate Airport
    @PatchMapping("/{id}/deactivate")
    public ApiResponse<AirportResponse> deactivateAirport(
            @PathVariable Long id
    ) {

        AirportResponse airportResponse =
                airportService.deactivateAirport(id);

        return new ApiResponse<>(
                true,
                "Airport deactivated successfully",
                airportResponse
        );
    }

    // Activate Airport
    @PatchMapping("/{id}/activate")
    public ApiResponse<AirportResponse> activateAirport(
            @PathVariable Long id
    ) {

        AirportResponse airportResponse =
                airportService.activateAirport(id);

        return new ApiResponse<>(
                true,
                "Airport activated successfully",
                airportResponse
        );
    }
}