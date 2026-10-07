package com.airline.booking.controller;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.AirportResponse;
import com.airline.booking.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    @GetMapping
    public ApiResponse<List<AirportResponse>> getActiveAirports() {

        List<AirportResponse> airports =
                airportService.getActiveAirports();

        return new ApiResponse<>(
                true,
                "Airports fetched successfully",
                airports
        );
    }
}