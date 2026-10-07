package com.airline.booking.service;

import com.airline.booking.dto.LoginRequest;
import com.airline.booking.dto.LoginResponse;
import com.airline.booking.dto.RegisterRequest;
import com.airline.booking.dto.UserResponse;
import com.airline.booking.entity.Role;
import com.airline.booking.entity.User;
import com.airline.booking.exception.AppException;
import com.airline.booking.repository.RoleRepository;
import com.airline.booking.repository.UserRepository;
import com.airline.booking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "USER role not found"
                ));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getName())
                .enabled(savedUser.isEnabled())
                .emailVerified(savedUser.isEmailVerified())
                .build();
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new AppException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        if (!user.isEnabled()) {
            throw new AppException(
                    HttpStatus.FORBIDDEN,
                    "User account is disabled"
            );
        }

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .enabled(user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .build();

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().getName()
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(userResponse)
                .build();
    }
}