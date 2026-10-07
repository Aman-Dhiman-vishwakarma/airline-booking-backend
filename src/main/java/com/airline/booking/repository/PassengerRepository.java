package com.airline.booking.repository;

import com.airline.booking.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import com.airline.booking.enums.BookingStatus;
import com.airline.booking.enums.Gender;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

import java.util.List;

public interface PassengerRepository
        extends JpaRepository<Passenger, Long> {

    List<Passenger> findByBookingId(Long bookingId);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM Passenger p
            JOIN p.booking b
            WHERE LOWER(p.firstName) = LOWER(:firstName)
              AND LOWER(p.lastName) = LOWER(:lastName)
              AND p.dateOfBirth = :dateOfBirth
              AND p.gender = :gender
              AND b.flight.id = :flightId
              AND b.status = :status
            """)
    boolean existsPassengerOnFlight(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("dateOfBirth") LocalDate dateOfBirth,
            @Param("gender") Gender gender,
            @Param("flightId") Long flightId,
            @Param("status") BookingStatus status
    );

}