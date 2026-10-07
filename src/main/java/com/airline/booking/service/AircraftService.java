package com.airline.booking.service;

import com.airline.booking.dto.aircraft.AircraftRequest;
import com.airline.booking.dto.aircraft.AircraftResponse;
import com.airline.booking.dto.aircraft.SeatResponse;
import com.airline.booking.entity.Aircraft;
import com.airline.booking.entity.Airline;
import com.airline.booking.entity.Seat;
import com.airline.booking.enums.SeatClass;
import com.airline.booking.enums.SeatType;
import com.airline.booking.exception.AppException;
import com.airline.booking.repository.AircraftRepository;
import com.airline.booking.repository.AirlineRepository;
import com.airline.booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AirlineRepository airlineRepository;
    private final SeatRepository seatRepository;

    // CREATE AIRCRAFT
    @Transactional
    public AircraftResponse createAircraft(AircraftRequest request) {

        // Check duplicate registration number
        if (aircraftRepository.existsByRegistrationNumber(
                request.getRegistrationNumber()
        )) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Aircraft registration number already exists"
            );
        }

        // Find airline
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        // Validate total seats
        validateTotalSeats(request.getTotalSeats());

        // Create aircraft
        Aircraft aircraft = Aircraft.builder()
                .airline(airline)
                .registrationNumber(request.getRegistrationNumber())
                .model(request.getModel())
                .totalSeats(request.getTotalSeats())
                .build();

        aircraft = aircraftRepository.save(aircraft);

        // Generate physical seats
        List<Seat> seats = generateSeats(aircraft);

        seatRepository.saveAll(seats);

        return mapToResponse(aircraft);
    }


    // GET ALL AIRCRAFT
    @Transactional(readOnly = true)
    public List<AircraftResponse> getAllAircraft() {

        return aircraftRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // GET AIRCRAFT BY ID
    @Transactional(readOnly = true)
    public AircraftResponse getAircraftById(Long id) {

        Aircraft aircraft = findAircraftById(id);

        return mapToResponse(aircraft);
    }


    // GET SEATS OF AIRCRAFT
    @Transactional(readOnly = true)
    public List<SeatResponse> getAircraftSeats(Long aircraftId) {

        // First make sure aircraft exists
        findAircraftById(aircraftId);

        return seatRepository.findByAircraftId(aircraftId)
                .stream()
                .map(this::mapSeatToResponse)
                .toList();
    }


    // UPDATE AIRCRAFT
    @Transactional
    public AircraftResponse updateAircraft(
            Long id,
            AircraftRequest request
    ) {

        Aircraft aircraft = findAircraftById(id);

        // Check whether another aircraft
        // already has this registration number
        if (aircraftRepository.existsByRegistrationNumberAndIdNot(
                request.getRegistrationNumber(),
                id
        )) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Aircraft registration number already exists"
            );
        }

        // Find new airline
        Airline airline = airlineRepository.findById(request.getAirlineId())
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Airline not found"
                ));

        // We don't allow totalSeats modification here
        if (!aircraft.getTotalSeats().equals(request.getTotalSeats())) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Total seats cannot be changed after aircraft creation"
            );
        }

        aircraft.setAirline(airline);
        aircraft.setRegistrationNumber(
                request.getRegistrationNumber()
        );
        aircraft.setModel(request.getModel());
        aircraft.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        aircraftRepository.save(aircraft);

        return mapToResponse(aircraft);
    }


    // DEACTIVATE AIRCRAFT
    @Transactional
    public AircraftResponse deactivateAircraft(Long id) {

        Aircraft aircraft = findAircraftById(id);

        if (!aircraft.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Aircraft is already inactive"
            );
        }

        aircraft.setActive(false);
        aircraft.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        aircraftRepository.save(aircraft);

        return mapToResponse(aircraft);
    }


    // ACTIVATE AIRCRAFT
    @Transactional
    public AircraftResponse activateAircraft(Long id) {

        Aircraft aircraft = findAircraftById(id);

        if (aircraft.isActive()) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Aircraft is already active"
            );
        }

        aircraft.setActive(true);
        aircraft.setUpdatedAt(
                java.time.LocalDateTime.now()
        );

        aircraftRepository.save(aircraft);

        return mapToResponse(aircraft);
    }


    // FIND AIRCRAFT
    private Aircraft findAircraftById(Long id) {

        return aircraftRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Aircraft not found"
                ));
    }


    // VALIDATE TOTAL SEATS
    private void validateTotalSeats(Integer totalSeats) {

        if (totalSeats == null || totalSeats <= 0) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Total seats must be greater than 0"
            );
        }

        if (totalSeats % 6 != 0) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "Total seats must be divisible by 6"
            );
        }
    }


    // GENERATE SEATS
    private List<Seat> generateSeats(Aircraft aircraft) {

        List<Seat> seats = new ArrayList<>();

        int rows = aircraft.getTotalSeats() / 6;

        String[] seatLetters = {
                "A", "B", "C",
                "D", "E", "F"
        };

        for (int row = 1; row <= rows; row++) {

            for (String letter : seatLetters) {

                Seat seat = Seat.builder()
                        .aircraft(aircraft)
                        .seatNumber(row + letter)
                        .seatClass(SeatClass.ECONOMY)
                        .seatType(getSeatType(letter))
                        .active(true)
                        .build();

                seats.add(seat);
            }
        }

        return seats;
    }


    // GET SEAT TYPE
    private SeatType getSeatType(String letter) {

        return switch (letter) {

            case "A", "F" -> SeatType.WINDOW;

            case "C", "D" -> SeatType.AISLE;

            case "B", "E" -> SeatType.MIDDLE;

            default -> throw new IllegalArgumentException(
                    "Invalid seat letter: " + letter
            );
        };
    }


    // AIRCRAFT → RESPONSE
    private AircraftResponse mapToResponse(Aircraft aircraft) {

        return AircraftResponse.builder()
                .id(aircraft.getId())
                .airlineId(aircraft.getAirline().getId())
                .airlineName(aircraft.getAirline().getName())
                .registrationNumber(
                        aircraft.getRegistrationNumber()
                )
                .model(aircraft.getModel())
                .totalSeats(aircraft.getTotalSeats())
                .active(aircraft.isActive())
                .createdAt(aircraft.getCreatedAt())
                .updatedAt(aircraft.getUpdatedAt())
                .build();
    }


    // SEAT → RESPONSE
    private SeatResponse mapSeatToResponse(Seat seat) {

        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .seatClass(seat.getSeatClass())
                .seatType(seat.getSeatType())
                .active(seat.isActive())
                .build();
    }
}