package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RefreshTokenUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private RefreshTokenUseCase refreshTokenUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshTokenUseCase = new RefreshTokenUseCase(userRepository, jwtService);
    }

    @Test
    void execute_ValidRefreshToken_ShouldReturnAccessToken() {
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(true)
            .build();

        when(jwtService.extractUsername(any())).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateAccessToken(1L, "test@example.com")).thenReturn("new-access-token");

        RefreshTokenUseCase.Command command = new RefreshTokenUseCase.Command("valid-refresh-token");
        RefreshTokenUseCase.Response response = refreshTokenUseCase.execute(command);

        assertNotNull(response.accessToken());
        assertEquals("new-access-token", response.accessToken());
        verify(userRepository).findById(1L);
        verify(jwtService).generateAccessToken(1L, "test@example.com");
    }

    @Test
    void execute_UserNotFound_ShouldThrowException() {
        when(jwtService.extractUsername(any())).thenReturn("999");
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        RefreshTokenUseCase.Command command = new RefreshTokenUseCase.Command("valid-refresh-token");

        assertThrows(UserNotFoundException.class, () -> refreshTokenUseCase.execute(command));
    }
}
