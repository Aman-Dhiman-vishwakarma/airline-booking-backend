package com.airline.booking.controller;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.flight.FlightResponse;
import com.airline.booking.dto.flight.FlightSearchRequest;
import com.airline.booking.dto.flight.FlightSeatResponse;
import com.airline.booking.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightSearchController {

    private final FlightService flightService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightResponse>>> searchFlights(
            @Valid @ModelAttribute FlightSearchRequest request
    ) {

        List<FlightResponse> response =
                flightService.searchFlights(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flights fetched successfully",
                        response
                )
        );
    }

    @GetMapping("/{flightId}/seats")
    public ResponseEntity<ApiResponse<List<FlightSeatResponse>>> getFlightSeats(
            @PathVariable Long flightId
    ) {

        List<FlightSeatResponse> response =
                flightService.getFlightSeats(flightId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight seats fetched successfully",
                        response
                )
        );
    }
}