package com.airline.booking.dto.aircraft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AircraftResponse {

    private Long id;

    private Long airlineId;

    private String airlineName;

    private String registrationNumber;

    private String model;

    private Integer totalSeats;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}