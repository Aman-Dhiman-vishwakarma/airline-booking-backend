package com.airline.booking.controller;

import com.airline.booking.dto.ApiResponse;
import com.airline.booking.dto.LoginRequest;
import com.airline.booking.dto.LoginResponse;
import com.airline.booking.dto.RegisterRequest;
import com.airline.booking.dto.UserResponse;
import com.airline.booking.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        UserResponse userResponse =
                authService.register(request);

        return new ApiResponse<>(
                true,
                "User registered successfully",
                userResponse
        );
    }

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        LoginResponse loginResponse =
                authService.login(request);

        ResponseCookie cookie = ResponseCookie
                .from("access_token", loginResponse.getAccessToken())
                .httpOnly(true)
                .secure(false) // localhost HTTP ke liye
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );

        return new ApiResponse<>(
                true,
                "Login successful",
                loginResponse.getUser()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletResponse response
    ) {

        Cookie cookie = new Cookie("access_token", null);

        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Logout successful",
                        null
                )
        );
    }
}