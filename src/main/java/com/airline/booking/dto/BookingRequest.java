package com.airline.booking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotEmpty(message = "At least one passenger is required")
    @Size(
            max = 9,
            message = "A booking cannot have more than 9 passengers"
    )
    @Valid
    private List<PassengerRequest> passengers;

    @NotNull(message = "Contact email is required")
    @Email(message = "Invalid contact email")
    private String contactEmail;

    @NotNull(message = "Contact phone is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Contact phone must contain exactly 10 digits"
    )
    private String contactPhone;
}