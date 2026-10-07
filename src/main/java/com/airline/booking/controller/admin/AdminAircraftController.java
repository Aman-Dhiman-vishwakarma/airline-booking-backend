package com.airline.booking.controller.admin;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.aircraft.AircraftRequest;
import com.airline.booking.dto.aircraft.AircraftResponse;
import com.airline.booking.dto.aircraft.SeatResponse;
import com.airline.booking.service.AircraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/aircraft")
@RequiredArgsConstructor
public class AdminAircraftController {

    private final AircraftService aircraftService;

    // CREATE AIRCRAFT
    @PostMapping
    public ResponseEntity<ApiResponse<AircraftResponse>> createAircraft(
            @Valid @RequestBody AircraftRequest request
    ) {

        AircraftResponse response =
                aircraftService.createAircraft(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Aircraft created successfully",
                        response
                ));
    }

    // GET ALL AIRCRAFT
    @GetMapping
    public ResponseEntity<ApiResponse<List<AircraftResponse>>> getAllAircraft() {

        List<AircraftResponse> response =
                aircraftService.getAllAircraft();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Aircraft fetched successfully",
                        response
                )
        );
    }

    // GET AIRCRAFT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AircraftResponse>> getAircraftById(
            @PathVariable Long id
    ) {

        AircraftResponse response =
                aircraftService.getAircraftById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Aircraft fetched successfully",
                        response
                )
        );
    }

    // GET AIRCRAFT SEATS
    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAircraftSeats(
            @PathVariable Long id
    ) {

        List<SeatResponse> response =
                aircraftService.getAircraftSeats(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Aircraft seats fetched successfully",
                        response
                )
        );
    }

    // UPDATE AIRCRAFT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AircraftResponse>> updateAircraft(
            @PathVariable Long id,
            @Valid @RequestBody AircraftRequest request
    ) {

        AircraftResponse response =
                aircraftService.updateAircraft(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Aircraft updated successfully",
                        response
                )
        );
    }

    // DEACTIVATE AIRCRAFT
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<AircraftResponse>> deactivateAircraft(
            @PathVariable Long id
    ) {

        AircraftResponse response =
                aircraftService.deactivateAircraft(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Aircraft deactivated successfully",
                        response
                )
        );
    }

    // ACTIVATE AIRCRAFT
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<AircraftResponse>> activateAircraft(
            @PathVariable Long id
    ) {

        AircraftResponse response =
                aircraftService.activateAircraft(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Aircraft activated successfully",
                        response
                )
        );
    }
}