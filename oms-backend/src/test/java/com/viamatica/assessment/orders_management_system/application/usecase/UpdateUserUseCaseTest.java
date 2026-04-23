package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditPort auditPort;

    private UpdateUserUseCase updateUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateUserUseCase = new UpdateUserUseCase(userRepository, auditPort);
    }

    @Test
    void execute_ValidUpdate_ShouldReturnUpdatedUser() {
        UserDomain existingUser = UserDomain.builder()
            .id(1L)
            .name("Old Name")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(UserRole.USER)
            .active(true)
            .createdAt(LocalDateTime.now())
            .build();

        UserDomain updatedUser = UserDomain.builder()
            .id(1L)
            .name("New Name")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(UserRole.ADMIN)
            .active(true)
            .createdAt(existingUser.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(updatedUser);

        UpdateUserUseCase.Command command = new UpdateUserUseCase.Command(
            1L,
            "New Name",
            UserRole.ADMIN,
            null
        );

        UserDomain result = updateUserUseCase.execute(command);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(UserRole.ADMIN, result.getRole());
        verify(userRepository).save(any());
        verify(auditPort).logEntityChange(any(), eq("users"), eq("UPDATE"), any(), any(), any());
    }

    @Test
    void execute_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateUserUseCase.Command command = new UpdateUserUseCase.Command(
            1L,
            "New Name",
            UserRole.ADMIN,
            null
        );

        assertThrows(UserNotFoundException.class, () -> updateUserUseCase.execute(command));
    }

    @Test
    void execute_InactiveUser_ShouldThrowException() {
        UserDomain inactiveUser = UserDomain.builder()
            .id(1L)
            .name("Inactive User")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(UserRole.USER)
            .active(false)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(inactiveUser));

        UpdateUserUseCase.Command command = new UpdateUserUseCase.Command(
            1L,
            "New Name",
            UserRole.ADMIN,
            null
        );

        assertThrows(IllegalArgumentException.class, () -> updateUserUseCase.execute(command));
    }
}
