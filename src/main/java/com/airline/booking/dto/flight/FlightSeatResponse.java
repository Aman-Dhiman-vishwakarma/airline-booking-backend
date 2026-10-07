package com.airline.booking.dto.flight;

import com.airline.booking.enums.SeatClass;
import com.airline.booking.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FlightSeatResponse {

    private Long seatId;

    private String seatNumber;

    private SeatClass seatClass;

    private SeatType seatType;

    private boolean active;

    private boolean available;
}