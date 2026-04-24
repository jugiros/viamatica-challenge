package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidPasswordException;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditPort auditPort;

    private AuthenticateUserUseCase authenticateUserUseCase;
    private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        authenticateUserUseCase = new AuthenticateUserUseCase(
            userRepository,
            auditPort,
            "TestSecretKeyForHS256AlgorithmAndItMustBeLongEnough",
            3600000L,
            7200000L
        );
    }

    @Test
    void execute_ValidCredentials_ShouldReturnTokens() {
        String hashedPassword = passwordEncoder.encode("password");
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(Email.of("test@example.com"))
            .passwordHash(hashedPassword)
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(true)
            .build();

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(user));

        AuthenticateUserUseCase.Command command = new AuthenticateUserUseCase.Command("test@example.com", "password");
        AuthenticateUserUseCase.Response response = authenticateUserUseCase.execute(command);

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals(user, response.user());
        verify(auditPort).logLogin(1L, "0.0.0.0", "API");
    }

    @Test
    void execute_UserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.empty());

        AuthenticateUserUseCase.Command command = new AuthenticateUserUseCase.Command("nonexistent@example.com", "password");

        assertThrows(UserNotFoundException.class, () -> authenticateUserUseCase.execute(command));
    }

    @Test
    void execute_InvalidPassword_ShouldThrowException() {
        String hashedPassword = passwordEncoder.encode("password");
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(Email.of("test@example.com"))
            .passwordHash(hashedPassword)
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(true)
            .build();

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(user));

        AuthenticateUserUseCase.Command command = new AuthenticateUserUseCase.Command("test@example.com", "wrongpassword");

        assertThrows(InvalidPasswordException.class, () -> authenticateUserUseCase.execute(command));
    }

    @Test
    void execute_InactiveUser_ShouldThrowException() {
        String hashedPassword = passwordEncoder.encode("password");
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(Email.of("test@example.com"))
            .passwordHash(hashedPassword)
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(false)
            .build();

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(user));

        AuthenticateUserUseCase.Command command = new AuthenticateUserUseCase.Command("test@example.com", "password");

        assertThrows(InvalidPasswordException.class, () -> authenticateUserUseCase.execute(command));
    }

    @Test
    void validateToken_ValidToken_ShouldReturnClaims() {
        String hashedPassword = passwordEncoder.encode("password");
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(Email.of("test@example.com"))
            .passwordHash(hashedPassword)
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(true)
            .build();

        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.of(user));

        AuthenticateUserUseCase.Command command = new AuthenticateUserUseCase.Command("test@example.com", "password");
        AuthenticateUserUseCase.Response response = authenticateUserUseCase.execute(command);

        Claims claims = authenticateUserUseCase.validateToken(response.accessToken());
        assertNotNull(claims);
        assertEquals("1", claims.getSubject());
        assertEquals("test@example.com", claims.get("email"));
    }
}
