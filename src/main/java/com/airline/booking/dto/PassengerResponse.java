package com.airline.booking.dto;

import com.airline.booking.dto.booking.BookingSeatResponse;
import com.airline.booking.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class PassengerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private BookingSeatResponse seat;
}