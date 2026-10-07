package com.airline.booking.dto;

import com.airline.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;

    private String bookingReference;

    private Long flightId;
    private String flightNumber;

    private BookingStatus status;

    private String contactEmail;
    private String contactPhone;

    private BigDecimal totalAmount;

    private List<PassengerResponse> passengers;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}