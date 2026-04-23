package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.application.usecase.AuthenticateUserUseCase;
import com.viamatica.assessment.orders_management_system.application.usecase.RefreshTokenUseCase;
import com.viamatica.assessment.orders_management_system.application.usecase.RegisterUserUseCase;
import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticateUserUseCase.Command command = new AuthenticateUserUseCase.Command(
                request.email(),
                request.password()
        );
        AuthenticateUserUseCase.Response response = authenticateUserUseCase.execute(command);
        return ResponseEntity.ok(new AuthResponse(
                response.accessToken(),
                response.refreshToken(),
                3600L
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                request.name(),
                request.email(),
                request.password(),
                request.role() != null ? UserRole.valueOf(request.role()) : UserRole.USER
        );
        UserDomain user = registerUserUseCase.execute(command);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenUseCase.Command command = new RefreshTokenUseCase.Command(request.refreshToken());
        RefreshTokenUseCase.Response response = refreshTokenUseCase.execute(command);
        return ResponseEntity.ok(new AuthResponse(response.accessToken(), null, 3600L));
    }

    private UserResponse toUserResponse(UserDomain user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail().value(),
                user.getRole().name(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
