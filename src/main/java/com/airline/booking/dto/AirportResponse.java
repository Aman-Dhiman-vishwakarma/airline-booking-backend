package com.airline.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AirportResponse {

    private Long id;
    private String name;
    private String code;
    private String city;
    private String country;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}