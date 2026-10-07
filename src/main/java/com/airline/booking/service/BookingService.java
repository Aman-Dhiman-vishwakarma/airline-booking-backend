package com.airline.booking.service;

import com.airline.booking.dto.BookingRequest;
import com.airline.booking.dto.PassengerRequest;
import com.airline.booking.dto.BookingResponse;
import com.airline.booking.dto.booking.BookingSeatResponse;
import com.airline.booking.dto.PassengerResponse;
import com.airline.booking.entity.Booking;
import com.airline.booking.entity.BookingSeat;
import com.airline.booking.entity.Flight;
import com.airline.booking.entity.Passenger;
import com.airline.booking.entity.Seat;
import com.airline.booking.entity.User;
import com.airline.booking.enums.BookingStatus;
import com.airline.booking.enums.FlightStatus;
import com.airline.booking.exception.AppException;
import com.airline.booking.repository.BookingRepository;
import com.airline.booking.repository.BookingSeatRepository;
import com.airline.booking.repository.FlightRepository;
import com.airline.booking.repository.PassengerRepository;
import com.airline.booking.repository.SeatRepository;
import com.airline.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    // ============================================================
    // CREATE BOOKING
    // ============================================================

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        // --------------------------------------------------------
        // 1. Get currently authenticated user
        // --------------------------------------------------------

        User user = getAuthenticatedUser();


        // --------------------------------------------------------
        // 2. Find flight
        // --------------------------------------------------------

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Flight not found"
                ));


        // --------------------------------------------------------
        // 3. Validate whether flight can be booked
        // --------------------------------------------------------

        validateFlightForBooking(flight);


        // --------------------------------------------------------
        // 4. Validate flight fare
        // --------------------------------------------------------

        if (flight.getBaseFare() == null
                || flight.getBaseFare().signum() <= 0) {

            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Flight fare is not available"
            );
        }

        for (PassengerRequest passenger : request.getPassengers()) {

            boolean alreadyBooked =
                    passengerRepository.existsPassengerOnFlight(
                            passenger.getFirstName().trim(),
                            passenger.getLastName().trim(),
                            passenger.getDateOfBirth(),
                            passenger.getGender(),
                            flight.getId(),
                            BookingStatus.CONFIRMED
                    );

            if (alreadyBooked) {
                throw new AppException(
                        HttpStatus.CONFLICT,
                        passenger.getFirstName()
                                + " "
                                + passenger.getLastName()
                                + " already has a booking for this flight"
                );
            }
        }

        // --------------------------------------------------------
        // 5. Get passenger count
        // --------------------------------------------------------

        int passengerCount = request.getPassengers().size();


        // --------------------------------------------------------
// 6. Find candidate available seats
// --------------------------------------------------------

        Long aircraftId = flight.getAircraft().getId();

        List<Seat> candidateSeats =
                seatRepository.findAvailableSeatsForFlight(
                        aircraftId,
                        flight.getId()
                );


// --------------------------------------------------------
// 7. Check whether enough candidate seats exist
// --------------------------------------------------------

        if (candidateSeats.size() < passengerCount) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Not enough seats available for all passengers"
            );
        }


// --------------------------------------------------------
// 8. Lock seats and perform final availability check
// --------------------------------------------------------

        List<Seat> allocatedSeats = new java.util.ArrayList<>();


        for (Seat candidateSeat : candidateSeats) {

            if (allocatedSeats.size() == passengerCount) {
                break;
            }

            Seat lockedSeat =
                    seatRepository.findActiveSeatForUpdate(
                                    candidateSeat.getId()
                            )
                            .orElseThrow(() -> new AppException(
                                    HttpStatus.CONFLICT,
                                    "Seat is no longer available"
                            ));


            boolean alreadyBooked =
                    bookingSeatRepository.existsByFlightIdAndSeatId(
                            flight.getId(),
                            lockedSeat.getId()
                    );


            if (!alreadyBooked) {
                allocatedSeats.add(lockedSeat);
            }
        }


// --------------------------------------------------------
// 9. Final availability check
// --------------------------------------------------------

        if (allocatedSeats.size() < passengerCount) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Not enough seats available for all passengers"
            );
        }


        // --------------------------------------------------------
        // 8. Calculate total amount
        // --------------------------------------------------------

        BigDecimal totalAmount =
                flight.getBaseFare()
                        .multiply(
                                BigDecimal.valueOf(passengerCount)
                        );


        // --------------------------------------------------------
        // 9. Create Booking
        // --------------------------------------------------------

        Booking booking = Booking.builder()
                .bookingReference(generateBookingReference())
                .user(user)
                .flight(flight)
                .status(BookingStatus.CONFIRMED)
                .contactEmail(request.getContactEmail().trim())
                .contactPhone(request.getContactPhone().trim())
                .totalAmount(totalAmount)
                .build();


        // --------------------------------------------------------
        // 10. Create passengers + automatically assign seats
        // --------------------------------------------------------

        for (int i = 0; i < passengerCount; i++) {

            PassengerRequest passengerRequest =
                    request.getPassengers().get(i);

            Seat assignedSeat = allocatedSeats.get(i);


            // ----------------------------------------------------
            // Create Passenger
            // ----------------------------------------------------

            Passenger passenger = Passenger.builder()
                    .booking(booking)
                    .firstName(
                            passengerRequest.getFirstName().trim()
                    )
                    .lastName(
                            passengerRequest.getLastName().trim()
                    )
                    .gender(passengerRequest.getGender())
                    .dateOfBirth(passengerRequest.getDateOfBirth())
                    .build();


            booking.getPassengers().add(passenger);


            // ----------------------------------------------------
            // Create BookingSeat
            // ----------------------------------------------------

            BookingSeat bookingSeat = BookingSeat.builder()
                    .booking(booking)
                    .passenger(passenger)
                    .flight(flight)
                    .seat(assignedSeat)
                    .build();


            booking.getSeats().add(bookingSeat);
        }


        // --------------------------------------------------------
        // 11. Save everything
        // --------------------------------------------------------

        booking = bookingRepository.save(booking);


        // --------------------------------------------------------
        // 12. Convert entity to response
        // --------------------------------------------------------

        return mapToResponse(booking);
    }


    // ============================================================
    // GET MY BOOKINGS
    // ============================================================

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {

        User user = getAuthenticatedUser();

        return bookingRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // GET MY BOOKINGS BY STATUS
    // ============================================================

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookingsByStatus(
            BookingStatus status
    ) {

        User user = getAuthenticatedUser();

        return bookingRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(
                        user.getId(),
                        status
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // GET BOOKING BY ID
    // ============================================================

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {

        User user = getAuthenticatedUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));

        validateBookingOwnership(booking, user);

        return mapToResponse(booking);
    }


    // ============================================================
    // GET BOOKING BY REFERENCE
    // ============================================================

    @Transactional(readOnly = true)
    public BookingResponse getBookingByReference(
            String bookingReference
    ) {

        User user = getAuthenticatedUser();

        Booking booking =
                bookingRepository.findByBookingReference(
                                bookingReference
                        )
                        .orElseThrow(() -> new AppException(
                                HttpStatus.NOT_FOUND,
                                "Booking not found"
                        ));

        validateBookingOwnership(booking, user);

        return mapToResponse(booking);
    }


    // ============================================================
    // CANCEL BOOKING
    // ============================================================

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {

        User user = getAuthenticatedUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));


        // --------------------------------------------------------
        // Check ownership
        // --------------------------------------------------------

        validateBookingOwnership(booking, user);


        // --------------------------------------------------------
        // Already cancelled?
        // --------------------------------------------------------

        if (booking.getStatus() == BookingStatus.CANCELLED) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Booking is already cancelled"
            );
        }


        // --------------------------------------------------------
        // Get flight
        // --------------------------------------------------------

        FlightStatus flightStatus =
                booking.getFlight().getStatus();


        // --------------------------------------------------------
        // Cannot cancel after departure
        // --------------------------------------------------------

        if (flightStatus == FlightStatus.DEPARTED
                || flightStatus == FlightStatus.ARRIVED) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Booking cannot be cancelled after flight departure"
            );
        }


        // --------------------------------------------------------
        // Cannot cancel while boarding
        // --------------------------------------------------------

        if (flightStatus == FlightStatus.BOARDING) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Booking cannot be cancelled while flight is boarding"
            );
        }


        // --------------------------------------------------------
        // Release seats
        // --------------------------------------------------------

        bookingSeatRepository.deleteByBookingId(
                booking.getId()
        );


        // --------------------------------------------------------
        // Cancel booking
        // --------------------------------------------------------

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);


        return mapToResponse(booking);
    }


    // ============================================================
    // AUTHENTICATED USER
    // ============================================================

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new AppException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }


        String email = authentication.getName();


        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));
    }


    // ============================================================
    // FLIGHT VALIDATION
    // ============================================================

    private void validateFlightForBooking(
            Flight flight
    ) {

        if (!flight.isActive()) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight is inactive"
            );
        }


        FlightStatus status = flight.getStatus();


        if (status == FlightStatus.CANCELLED) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight has been cancelled"
            );
        }


        if (status == FlightStatus.BOARDING) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight is currently boarding"
            );
        }


        if (status == FlightStatus.DEPARTED) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight has already departed"
            );
        }


        if (status == FlightStatus.ARRIVED) {

            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight has already arrived"
            );
        }
    }


    // ============================================================
    // BOOKING OWNERSHIP
    // ============================================================

    private void validateBookingOwnership(
            Booking booking,
            User user
    ) {

        if (!booking.getUser().getId().equals(user.getId())) {

            throw new AppException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this booking"
            );
        }
    }


    // ============================================================
    // BOOKING REFERENCE
    // ============================================================

    private String generateBookingReference() {

        String reference;

        do {

            reference = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 10)
                    .toUpperCase();

        } while (
                bookingRepository.existsByBookingReference(
                        reference
                )
        );

        return reference;
    }


    // ============================================================
    // ENTITY → RESPONSE
    // ============================================================

    private BookingResponse mapToResponse(
            Booking booking
    ) {

        /*
         * Create a map:
         *
         * Passenger ID → BookingSeat
         *
         * Example:
         *
         * 1 → 12A
         * 2 → 12B
         */

        Map<Long, BookingSeat> seatByPassengerId =
                new HashMap<>();


        for (BookingSeat bookingSeat : booking.getSeats()) {

            Long passengerId =
                    bookingSeat.getPassenger().getId();

            seatByPassengerId.put(
                    passengerId,
                    bookingSeat
            );
        }


        // --------------------------------------------------------
        // Map passengers
        // --------------------------------------------------------

        List<PassengerResponse> passengers =
                booking.getPassengers()
                        .stream()
                        .map(passenger -> {

                            BookingSeat bookingSeat =
                                    seatByPassengerId.get(
                                            passenger.getId()
                                    );


                            BookingSeatResponse seatResponse =
                                    null;


                            if (bookingSeat != null) {

                                Seat seat =
                                        bookingSeat.getSeat();


                                seatResponse =
                                        BookingSeatResponse.builder()
                                                .seatId(seat.getId())
                                                .seatNumber(
                                                        seat.getSeatNumber()
                                                )
                                                .seatClass(
                                                        seat.getSeatClass()
                                                )
                                                .seatType(
                                                        seat.getSeatType()
                                                )
                                                .build();
                            }


                            return PassengerResponse.builder()
                                    .id(passenger.getId())
                                    .firstName(
                                            passenger.getFirstName()
                                    )
                                    .lastName(
                                            passenger.getLastName()
                                    )
                                    .gender(
                                            passenger.getGender()
                                    )
                                    .dateOfBirth(
                                            passenger.getDateOfBirth()
                                    )
                                    .seat(seatResponse)
                                    .build();

                        })
                        .toList();


        // --------------------------------------------------------
        // Build final booking response
        // --------------------------------------------------------

        return BookingResponse.builder()
                .id(booking.getId())
                .bookingReference(
                        booking.getBookingReference()
                )
                .flightId(
                        booking.getFlight().getId()
                )
                .flightNumber(
                        booking.getFlight().getFlightNumber()
                )
                .status(
                        booking.getStatus()
                )
                .contactEmail(
                        booking.getContactEmail()
                )
                .contactPhone(
                        booking.getContactPhone()
                )
                .totalAmount(
                        booking.getTotalAmount()
                )
                .passengers(passengers)
                .createdAt(
                        booking.getCreatedAt()
                )
                .updatedAt(
                        booking.getUpdatedAt()
                )
                .build();
    }
}