package com.airline.booking.repository;

import com.airline.booking.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AirportRepository
        extends JpaRepository<Airport, Long> {

    Optional<Airport> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Airport> findByActiveTrueOrderByNameAsc();
}