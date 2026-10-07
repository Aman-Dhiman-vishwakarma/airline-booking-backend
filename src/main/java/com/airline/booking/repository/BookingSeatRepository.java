package com.airline.booking.repository;

import com.airline.booking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BookingSeatRepository
        extends JpaRepository<BookingSeat, Long> {

    boolean existsByFlightIdAndSeatId(
            Long flightId,
            Long seatId
    );

    boolean existsByBookingIdAndPassengerId(
            Long bookingId,
            Long passengerId
    );

    List<BookingSeat> findByBookingId(Long bookingId);

    List<BookingSeat> findByFlightId(Long flightId);

    @Query("""
            SELECT bs.seat.id
            FROM BookingSeat bs
            WHERE bs.flight.id = :flightId
            """)
    Set<Long> findBookedSeatIdsByFlightId(
            @Param("flightId") Long flightId
    );

    void deleteByBookingId(Long bookingId);
}