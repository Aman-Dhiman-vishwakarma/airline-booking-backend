package com.airline.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AirlineResponse {

    private Long id;
    private String name;
    private String code;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}