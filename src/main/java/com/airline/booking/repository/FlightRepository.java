package com.airline.booking.repository;

import com.airline.booking.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    boolean existsByFlightNumber(String flightNumber);

    boolean existsByAircraftIdAndDepartureTimeLessThanAndArrivalTimeGreaterThan(
            Long aircraftId,
            LocalDateTime arrivalTime,
            LocalDateTime departureTime
    );

    boolean existsByAircraftIdAndDepartureTimeLessThanAndArrivalTimeGreaterThanAndIdNot(
            Long aircraftId,
            LocalDateTime arrivalTime,
            LocalDateTime departureTime,
            Long id
    );

    @Query("""
        SELECT f
        FROM Flight f
        WHERE f.departureAirport.code = :departureAirportCode
          AND f.arrivalAirport.code = :arrivalAirportCode
          AND f.departureTime >= :startOfDay
          AND f.departureTime < :startOfNextDay
          AND f.active = true
          AND f.status <> com.airline.booking.enums.FlightStatus.CANCELLED
        ORDER BY f.departureTime ASC
        """)
    List<Flight> searchFlights(
            @Param("departureAirportCode") String departureAirportCode,
            @Param("arrivalAirportCode") String arrivalAirportCode,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("startOfNextDay") LocalDateTime startOfNextDay
    );
}