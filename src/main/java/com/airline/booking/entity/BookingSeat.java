package com.airline.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "booking_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_booking_seat_flight_seat",
                        columnNames = {"flight_id", "seat_id"}
                ),
                @UniqueConstraint(
                        name = "uk_booking_seat_booking_passenger",
                        columnNames = {"booking_id", "passenger_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_booking_seats_booking",
                        columnList = "booking_id"
                ),
                @Index(
                        name = "idx_booking_seats_flight",
                        columnList = "flight_id"
                ),
                @Index(
                        name = "idx_booking_seats_seat",
                        columnList = "seat_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;
}