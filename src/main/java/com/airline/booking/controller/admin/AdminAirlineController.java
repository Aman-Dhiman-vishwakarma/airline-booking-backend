package com.airline.booking.controller.admin;

import com.airline.booking.dto.AirlineRequest;
import com.airline.booking.dto.AirlineResponse;
import com.airline.booking.dto.ApiResponse;
import com.airline.booking.service.AirlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/airlines")
@RequiredArgsConstructor
public class AdminAirlineController {

    private final AirlineService airlineService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AirlineResponse> createAirline(
            @Valid @RequestBody AirlineRequest request
    ) {

        AirlineResponse airlineResponse =
                airlineService.createAirline(request);

        return new ApiResponse<>(
                true,
                "Airline created successfully",
                airlineResponse
        );
    }

    @GetMapping
    public ApiResponse<List<AirlineResponse>> getAllAirlines() {

        List<AirlineResponse> airlines =
                airlineService.getAllAirlines();

        return new ApiResponse<>(
                true,
                "Airlines fetched successfully",
                airlines
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AirlineResponse> getAirlineById(
            @PathVariable Long id
    ) {

        AirlineResponse airlineResponse =
                airlineService.getAirlineById(id);

        return new ApiResponse<>(
                true,
                "Airline fetched successfully",
                airlineResponse
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<AirlineResponse> updateAirline(
            @PathVariable Long id,
            @Valid @RequestBody AirlineRequest request
    ) {

        AirlineResponse airlineResponse =
                airlineService.updateAirline(id, request);

        return new ApiResponse<>(
                true,
                "Airline updated successfully",
                airlineResponse
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<AirlineResponse> deactivateAirline(
            @PathVariable Long id
    ) {

        AirlineResponse airlineResponse =
                airlineService.deactivateAirline(id);

        return new ApiResponse<>(
                true,
                "Airline deactivated successfully",
                airlineResponse
        );
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<AirlineResponse> activateAirline(
            @PathVariable Long id
    ) {

        AirlineResponse airlineResponse =
                airlineService.activateAirline(id);

        return new ApiResponse<>(
                true,
                "Airline activated successfully",
                airlineResponse
        );
    }
}