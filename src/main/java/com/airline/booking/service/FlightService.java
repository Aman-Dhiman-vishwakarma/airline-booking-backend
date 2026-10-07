package com.airline.booking.service;

import com.airline.booking.dto.flight.FlightRequest;
import com.airline.booking.dto.flight.FlightResponse;
import com.airline.booking.dto.flight.FlightSearchRequest;
import com.airline.booking.entity.Aircraft;
import com.airline.booking.entity.Airline;
import com.airline.booking.entity.Airport;
import com.airline.booking.entity.Flight;
import com.airline.booking.enums.FlightStatus;
import com.airline.booking.exception.AppException;
import com.airline.booking.repository.AircraftRepository;
import com.airline.booking.repository.AirlineRepository;
import com.airline.booking.repository.AirportRepository;
import com.airline.booking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airline.booking.dto.flight.FlightSeatResponse;
import com.airline.booking.entity.Seat;
import com.airline.booking.repository.BookingSeatRepository;
import com.airline.booking.repository.SeatRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AircraftRepository aircraftRepository;
    private final AirportRepository airportRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    @Transactional
    public FlightResponse createFlight(FlightRequest request) {

        // 1. Validate flight number
        validateFlightNumber(request.getFlightNumber());

        // 2. Validate departure and arrival time
        validateFlightTimes(
                request.getDepartureTime(),
                request.getArrivalTime()
        );

        // 3. Find and validate airline
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        if (!airline.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Airline is inactive"
            );
        }

        // 4. Find and validate aircraft
        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Aircraft not found"
                ));

        if (!aircraft.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Aircraft is inactive"
            );
        }

        // 5. Aircraft must belong to selected airline
        if (!aircraft.getAirline().getId().equals(airline.getId())) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Aircraft does not belong to the selected airline"
            );
        }

        // 6. Find and validate departure airport
        Airport departureAirport = airportRepository
                .findById(request.getDepartureAirportId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Departure airport not found"
                ));

        if (!departureAirport.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure airport is inactive"
            );
        }

        // 7. Find and validate arrival airport
        Airport arrivalAirport = airportRepository
                .findById(request.getArrivalAirportId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Arrival airport not found"
                ));

        if (!arrivalAirport.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival airport is inactive"
            );
        }

        // 8. Departure and arrival cannot be same
        if (departureAirport.getId().equals(arrivalAirport.getId())) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure and arrival airports cannot be the same"
            );
        }

        // 9. Check aircraft schedule conflict
        boolean scheduleConflict =
                flightRepository
                        .existsByAircraftIdAndDepartureTimeLessThanAndArrivalTimeGreaterThan(
                                aircraft.getId(),
                                request.getArrivalTime(),
                                request.getDepartureTime()
                        );

        if (scheduleConflict) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Aircraft already has a flight scheduled during this time"
            );
        }

        // 10. Create flight
        Flight flight = Flight.builder()
                .flightNumber(request.getFlightNumber())
                .airline(airline)
                .aircraft(aircraft)
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .baseFare(request.getBaseFare())
                .status(FlightStatus.SCHEDULED)
                .active(true)
                .build();

        flight = flightRepository.save(flight);

        return mapToResponse(flight);
    }

    @Transactional(readOnly = true)
    public List<FlightResponse> getAllFlights() {

        return flightRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FlightResponse getFlightById(Long id) {

        Flight flight = findFlightById(id);

        return mapToResponse(flight);
    }

    @Transactional
    public FlightResponse updateFlight(
            Long id,
            FlightRequest request
    ) {

        // 1. Find existing flight
        Flight flight = findFlightById(id);

        // 2. Validate flight number
        validateFlightNumberForUpdate(
                request.getFlightNumber(),
                flight
        );

        // 3. Validate time
        validateFlightTimes(
                request.getDepartureTime(),
                request.getArrivalTime()
        );

        // 4. Find and validate airline
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        if (!airline.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Airline is inactive"
            );
        }

        // 5. Find and validate aircraft
        Aircraft aircraft = aircraftRepository.findById(request.getAircraftId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Aircraft not found"
                ));

        if (!aircraft.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Aircraft is inactive"
            );
        }

        // 6. Aircraft must belong to selected airline
        if (!aircraft.getAirline().getId().equals(airline.getId())) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Aircraft does not belong to the selected airline"
            );
        }

        // 7. Find and validate departure airport
        Airport departureAirport = airportRepository
                .findById(request.getDepartureAirportId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Departure airport not found"
                ));

        if (!departureAirport.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure airport is inactive"
            );
        }

        // 8. Find and validate arrival airport
        Airport arrivalAirport = airportRepository
                .findById(request.getArrivalAirportId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Arrival airport not found"
                ));

        if (!arrivalAirport.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival airport is inactive"
            );
        }

        // 9. Departure and arrival cannot be same
        if (departureAirport.getId().equals(arrivalAirport.getId())) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure and arrival airports cannot be the same"
            );
        }

        // 10. Check aircraft schedule conflict
        // Exclude current flight using its ID
        boolean scheduleConflict =
                flightRepository
                        .existsByAircraftIdAndDepartureTimeLessThanAndArrivalTimeGreaterThanAndIdNot(
                                aircraft.getId(),
                                request.getArrivalTime(),
                                request.getDepartureTime(),
                                id
                        );

        if (scheduleConflict) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Aircraft already has another flight scheduled during this time"
            );
        }

        // 11. Update flight
        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirline(airline);
        flight.setAircraft(aircraft);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setBaseFare(request.getBaseFare());
        flight.setUpdatedAt(LocalDateTime.now());

        flightRepository.save(flight);

        return mapToResponse(flight);
    }

    @Transactional
    public FlightResponse deactivateFlight(Long id) {

        Flight flight = findFlightById(id);

        if (!flight.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight is already inactive"
            );
        }

        flight.setActive(false);
        flight.setUpdatedAt(LocalDateTime.now());

        flightRepository.save(flight);

        return mapToResponse(flight);
    }

    @Transactional
    public FlightResponse activateFlight(Long id) {

        Flight flight = findFlightById(id);

        if (flight.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight is already active"
            );
        }

        flight.setActive(true);
        flight.setUpdatedAt(LocalDateTime.now());

        flightRepository.save(flight);

        return mapToResponse(flight);
    }

    private Flight findFlightById(Long id) {

        return flightRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Flight not found"
                ));
    }

    private void validateFlightNumber(String flightNumber) {

        if (flightRepository.existsByFlightNumber(flightNumber)) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight number already exists"
            );
        }
    }

    private void validateFlightNumberForUpdate(
            String flightNumber,
            Flight currentFlight
    ) {

        // Same flight number is allowed for current flight
        if (currentFlight.getFlightNumber().equals(flightNumber)) {
            return;
        }

        // Check whether another flight already uses this number
        if (flightRepository.existsByFlightNumber(flightNumber)) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Flight number already exists"
            );
        }
    }

    private void validateFlightTimes(
            LocalDateTime departureTime,
            LocalDateTime arrivalTime
    ) {

        if (!departureTime.isBefore(arrivalTime)) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure time must be before arrival time"
            );
        }
    }

    private FlightResponse mapToResponse(Flight flight) {

        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())

                .airlineId(flight.getAirline().getId())
                .airlineName(flight.getAirline().getName())
                .airlineCode(flight.getAirline().getCode())

                .aircraftId(flight.getAircraft().getId())
                .aircraftModel(flight.getAircraft().getModel())
                .registrationNumber(
                        flight.getAircraft().getRegistrationNumber()
                )

                .departureAirportId(
                        flight.getDepartureAirport().getId()
                )
                .departureAirportName(
                        flight.getDepartureAirport().getName()
                )
                .departureAirportCode(
                        flight.getDepartureAirport().getCode()
                )

                .arrivalAirportId(
                        flight.getArrivalAirport().getId()
                )
                .arrivalAirportName(
                        flight.getArrivalAirport().getName()
                )
                .arrivalAirportCode(
                        flight.getArrivalAirport().getCode()
                )

                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())

                .baseFare(flight.getBaseFare())

                .status(flight.getStatus())
                .active(flight.isActive())

                .createdAt(flight.getCreatedAt())
                .updatedAt(flight.getUpdatedAt())
                .build();
    }

    @Transactional
    public FlightResponse updateFlightStatus(
            Long id,
            FlightStatus newStatus
    ) {

        Flight flight = findFlightById(id);

        FlightStatus currentStatus = flight.getStatus();

        validateStatusTransition(currentStatus, newStatus);

        flight.setStatus(newStatus);
        flight.setUpdatedAt(LocalDateTime.now());

        flightRepository.save(flight);

        return mapToResponse(flight);
    }

    private void validateStatusTransition(
            FlightStatus currentStatus,
            FlightStatus newStatus
    ) {

        boolean validTransition = switch (currentStatus) {

            case SCHEDULED ->
                    newStatus == FlightStatus.BOARDING
                            || newStatus == FlightStatus.DELAYED
                            || newStatus == FlightStatus.CANCELLED;

            case BOARDING ->
                    newStatus == FlightStatus.DEPARTED;

            case DEPARTED ->
                    newStatus == FlightStatus.ARRIVED;

            case DELAYED ->
                    newStatus == FlightStatus.BOARDING
                            || newStatus == FlightStatus.CANCELLED;

            case ARRIVED, CANCELLED ->
                    false;
        };

        if (!validTransition) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid flight status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    @Transactional(readOnly = true)
    public List<FlightResponse> searchFlights(
            FlightSearchRequest request
    ) {

        String departureAirportCode =
                request.getDepartureAirportCode().trim().toUpperCase();

        String arrivalAirportCode =
                request.getArrivalAirportCode().trim().toUpperCase();

        if (departureAirportCode.equals(arrivalAirportCode)) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure and arrival airports cannot be the same"
            );
        }

        Airport departureAirport = airportRepository
                .findByCode(departureAirportCode)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Departure airport not found"
                ));

        if (!departureAirport.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure airport is inactive"
            );
        }

        Airport arrivalAirport = airportRepository
                .findByCode(arrivalAirportCode)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Arrival airport not found"
                ));

        if (!arrivalAirport.isActive()) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Arrival airport is inactive"
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate departureDate = request.getDepartureDate();

        // Past date is not allowed
        if (departureDate.isBefore(today)) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Departure date cannot be in the past"
            );
        }

        LocalDateTime startOfNextDay =
                departureDate
                        .plusDays(1)
                        .atStartOfDay();

        LocalDateTime startTime;

        if (departureDate.isEqual(today)) {
            // Today: only flights from current time onwards
            startTime = LocalDateTime.now();
        } else {
            // Future date: search from beginning of that day
            startTime = departureDate.atStartOfDay();
        }

        return flightRepository
                .searchFlights(
                        departureAirportCode,
                        arrivalAirportCode,
                        startTime,
                        startOfNextDay
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<FlightSeatResponse> getFlightSeats(Long flightId) {

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Flight not found"
                ));

        Long aircraftId = flight.getAircraft().getId();

        List<Seat> seats =
                seatRepository.findByAircraftId(aircraftId);

        Set<Long> bookedSeatIds =
                bookingSeatRepository.findBookedSeatIdsByFlightId(flightId);

        return seats.stream()
                .map(seat -> FlightSeatResponse.builder()
                        .seatId(seat.getId())
                        .seatNumber(seat.getSeatNumber())
                        .seatClass(seat.getSeatClass())
                        .seatType(seat.getSeatType())
                        .active(seat.isActive())
                        .available(
                                seat.isActive()
                                        && !bookedSeatIds.contains(seat.getId())
                        )
                        .build()
                )
                .toList();
    }
}