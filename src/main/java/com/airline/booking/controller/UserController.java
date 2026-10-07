package com.airline.booking.controller;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.UserResponse;
import com.airline.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(
            Authentication authentication
    ) {

        UserResponse userResponse =
                userService.getCurrentUser(authentication);

        return new ApiResponse<>(
                true,
                "User fetched successfully",
                userResponse
        );
    }
}