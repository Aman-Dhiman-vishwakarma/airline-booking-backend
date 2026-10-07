package com.airline.booking.repository;

import com.airline.booking.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByAircraftId(Long aircraftId);

    boolean existsByAircraftIdAndSeatNumber(
            Long aircraftId,
            String seatNumber
    );

    @Query("""
            SELECT s
            FROM Seat s
            WHERE s.aircraft.id = :aircraftId
              AND s.active = true
              AND s.id NOT IN (
                  SELECT bs.seat.id
                  FROM BookingSeat bs
                  WHERE bs.flight.id = :flightId
              )
            ORDER BY s.id ASC
            """)
    List<Seat> findAvailableSeatsForFlight(
            @Param("aircraftId") Long aircraftId,
            @Param("flightId") Long flightId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Seat s
            WHERE s.id = :seatId
              AND s.active = true
            """)
    Optional<Seat> findActiveSeatForUpdate(
            @Param("seatId") Long seatId
    );
}