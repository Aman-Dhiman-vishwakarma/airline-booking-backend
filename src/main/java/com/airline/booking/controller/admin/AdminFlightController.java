package com.airline.booking.controller.admin;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.flight.FlightRequest;
import com.airline.booking.dto.flight.FlightResponse;
import com.airline.booking.dto.flight.FlightStatusRequest;
import com.airline.booking.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/flights")
@RequiredArgsConstructor
public class AdminFlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<ApiResponse<FlightResponse>> createFlight(
            @Valid @RequestBody FlightRequest request
    ) {

        FlightResponse response = flightService.createFlight(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Flight created successfully",
                                response
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightResponse>>> getAllFlights() {

        List<FlightResponse> response = flightService.getAllFlights();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flights fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightResponse>> getFlightById(
            @PathVariable Long id
    ) {

        FlightResponse response = flightService.getFlightById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight fetched successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightResponse>> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequest request
    ) {

        FlightResponse response = flightService.updateFlight(
                id,
                request
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight updated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<FlightResponse>> deactivateFlight(
            @PathVariable Long id
    ) {

        FlightResponse response = flightService.deactivateFlight(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight deactivated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<FlightResponse>> activateFlight(
            @PathVariable Long id
    ) {

        FlightResponse response = flightService.activateFlight(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight activated successfully",
                        response
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FlightResponse>> updateFlightStatus(
            @PathVariable Long id,
            @Valid @RequestBody FlightStatusRequest request
    ) {

        FlightResponse response = flightService.updateFlightStatus(
                id,
                request.getStatus()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight status updated successfully",
                        response
                )
        );
    }
}