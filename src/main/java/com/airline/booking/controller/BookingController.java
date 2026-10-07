package com.airline.booking.controller;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.BookingRequest;
import com.airline.booking.dto.BookingResponse;
import com.airline.booking.enums.BookingStatus;
import com.airline.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    // ============================================================
    // CREATE BOOKING
    // ============================================================

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request
    ) {

        BookingResponse response =
                bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Booking created successfully",
                                response
                        )
                );
    }


    // ============================================================
    // GET MY BOOKINGS
    // ============================================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {

        List<BookingResponse> response =
                bookingService.getMyBookings();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Bookings fetched successfully",
                        response
                )
        );
    }


    // ============================================================
    // GET MY BOOKINGS BY STATUS
    // ============================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>>
    getMyBookingsByStatus(
            @PathVariable BookingStatus status
    ) {

        List<BookingResponse> response =
                bookingService.getMyBookingsByStatus(status);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Bookings fetched successfully",
                        response
                )
        );
    }


    // ============================================================
    // GET BOOKING BY ID
    // ============================================================

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long bookingId
    ) {

        BookingResponse response =
                bookingService.getBookingById(bookingId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking fetched successfully",
                        response
                )
        );
    }


    // ============================================================
    // GET BOOKING BY REFERENCE
    // ============================================================

    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<ApiResponse<BookingResponse>>
    getBookingByReference(
            @PathVariable String bookingReference
    ) {

        BookingResponse response =
                bookingService.getBookingByReference(
                        bookingReference
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking fetched successfully",
                        response
                )
        );
    }


    // ============================================================
    // CANCEL BOOKING
    // ============================================================

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>>
    cancelBooking(
            @PathVariable Long bookingId
    ) {

        BookingResponse response =
                bookingService.cancelBooking(bookingId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking cancelled successfully",
                        response
                )
        );
    }
}