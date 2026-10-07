package com.airline.booking.dto.aircraft;

import com.airline.booking.enums.SeatClass;
import com.airline.booking.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SeatResponse {

    private Long id;

    private String seatNumber;

    private SeatClass seatClass;

    private SeatType seatType;

    private boolean active;
}