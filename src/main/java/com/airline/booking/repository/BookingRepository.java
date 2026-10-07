package com.airline.booking.repository;

import com.airline.booking.entity.Booking;
import com.airline.booking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(
            String bookingReference
    );

    boolean existsByBookingReference(
            String bookingReference
    );

    List<Booking> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<Booking> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            BookingStatus status
    );
}