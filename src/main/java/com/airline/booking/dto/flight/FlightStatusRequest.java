package com.airline.booking.dto.flight;

import com.airline.booking.enums.FlightStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightStatusRequest {

    @NotNull(message = "Flight status is required")
    private FlightStatus status;
}