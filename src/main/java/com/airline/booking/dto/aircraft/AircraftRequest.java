package com.airline.booking.dto.aircraft;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AircraftRequest {

    @NotNull(message = "Airline ID is required")
    private Long airlineId;

    @NotBlank(message = "Registration number is required")
    @Size(max = 20, message = "Registration number must not exceed 20 characters")
    private String registrationNumber;

    @NotBlank(message = "Aircraft model is required")
    @Size(max = 50, message = "Aircraft model must not exceed 50 characters")
    private String model;

    @NotNull(message = "Total seats is required")
    private Integer totalSeats;
}