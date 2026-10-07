package com.airline.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirlineRequest {

    @NotBlank(message = "Airline name is required")
    @Size(max = 100, message = "Airline name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Airline code is required")
    @Size(max = 10, message = "Airline code must not exceed 10 characters")
    private String code;
}