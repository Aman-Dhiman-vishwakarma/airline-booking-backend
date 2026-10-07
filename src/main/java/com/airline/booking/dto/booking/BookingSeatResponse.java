package com.airline.booking.dto.booking;

import com.airline.booking.enums.SeatClass;
import com.airline.booking.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//@Getter
//@AllArgsConstructor
//@Builder
//public class BookingSeatResponse {
//
//    private Long id;
//
//    private Long passengerId;
//
//    private String passengerName;
//
//    private Long seatId;
//
//    private String seatNumber;
//
//    private SeatClass seatClass;
//
//    private SeatType seatType;
//}

@Getter
@AllArgsConstructor
@Builder
public class BookingSeatResponse {

    private Long seatId;
    private String seatNumber;
    private SeatClass seatClass;
    private SeatType seatType;
}