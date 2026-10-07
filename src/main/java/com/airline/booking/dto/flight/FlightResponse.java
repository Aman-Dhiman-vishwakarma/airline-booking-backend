package com.airline.booking.dto.flight;

import com.airline.booking.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class FlightResponse {

    private Long id;

    private String flightNumber;

    private Long airlineId;
    private String airlineName;
    private String airlineCode;

    private Long aircraftId;
    private String aircraftModel;
    private String registrationNumber;

    private Long departureAirportId;
    private String departureAirportName;
    private String departureAirportCode;

    private Long arrivalAirportId;
    private String arrivalAirportName;
    private String arrivalAirportCode;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private FlightStatus status;

    private boolean active;
    private BigDecimal baseFare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}