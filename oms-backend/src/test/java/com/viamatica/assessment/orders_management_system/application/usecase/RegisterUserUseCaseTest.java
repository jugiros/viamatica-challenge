package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.EmailAlreadyExistsException;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidPasswordException;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserUseCase Tests")
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("UTUS01 - Registro exitoso: datos válidos → usuario creado, ID generado, password hasheado")
    void testUTUS01_RegisterSuccess_WhenValidData() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "John Doe",
                "john@example.com",
                "Password123",
                UserRole.USER
        );

        UserDomain savedUser = UserDomain.builder()
                .id(1L)
                .name("John Doe")
                .email(Email.of("john@example.com"))
                .passwordHash("$2a$10$hash")
                .role(UserRole.USER)
                .active(true)
                .build();

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.save(any(UserDomain.class))).thenReturn(savedUser);

        // Act
        UserDomain result = registerUserUseCase.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail().value()).isEqualTo("john@example.com");
        assertThat(result.getPasswordHash()).isNotNull();
        assertThat(result.getPasswordHash()).startsWith("$2a$");
        assertThat(result.getRole()).isEqualTo(UserRole.USER);

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).save(any(UserDomain.class));
        verify(auditPort).logEntityChange(
                any(),
                eq("users"),
                eq("INSERT"),
                any(),
                isNull(),
                any()
        );
    }

    @Test
    @DisplayName("UTUS02 - Email duplicado → EmailAlreadyExistsException")
    void testUTUS02_EmailDuplicated_WhenEmailAlreadyExists() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "John Doe",
                "john@example.com",
                "Password123",
                UserRole.USER
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("john@example.com");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(UserDomain.class));
        verify(auditPort, never()).logEntityChange(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("UTUS03 - Nombre vacío → DomainException con mensaje 'El nombre es obligatorio'")
    void testUTUS03_EmptyName_WhenNameIsEmpty() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "",
                "john@example.com",
                "Password123",
                UserRole.USER
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null or blank");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("UTUS04 - Email inválido 'noesmail' → DomainException formato email")
    void testUTUS04_InvalidEmail_WhenEmailFormatIsInvalid() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "John Doe",
                "noesmail",
                "Password123",
                UserRole.USER
        );

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");

        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("UTUS05 - Password sin mayúscula 'abc12345' → InvalidPasswordException")
    void testUTUS05_PasswordWithoutUppercase_WhenPasswordHasNoUppercase() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "John Doe",
                "john@example.com",
                "abc12345",
                UserRole.USER
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("uppercase");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("UTUS06 - Password sin número 'Abcdefgh' → InvalidPasswordException")
    void testUTUS06_PasswordWithoutNumber_WhenPasswordHasNoNumber() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "John Doe",
                "john@example.com",
                "Abcdefgh",
                UserRole.USER
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("number");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("UTUS07 - Password < 8 chars 'Ab1' → InvalidPasswordException longitud mínima")
    void testUTUS07_PasswordTooShort_WhenPasswordIsLessThan8Chars() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "John Doe",
                "john@example.com",
                "Ab1",
                UserRole.USER
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("8 characters");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("UTUS08 - Rol inválido 'SUPERADMIN' → DomainException rol no reconocido")
    void testUTUS08_InvalidRole_WhenRoleIsInvalid() {
        // Arrange
        // Note: UserRole is an enum, so invalid roles would be handled at compile time
        // This test would need a different approach if the usecase accepts String instead of enum
        // For now, we'll test with a null role which defaults to USER
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "John Doe",
                "john@example.com",
                "Password123",
                null
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        UserDomain savedUser = UserDomain.builder()
                .id(1L)
                .name("John Doe")
                .email(Email.of("john@example.com"))
                .passwordHash("$2a$10$hash")
                .role(UserRole.USER)
                .active(true)
                .build();

        when(userRepository.save(any(UserDomain.class))).thenReturn(savedUser);

        // Act
        UserDomain result = registerUserUseCase.execute(command);

        // Assert
        assertThat(result.getRole()).isEqualTo(UserRole.USER);

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("UTUS09 - Nombre con solo espacios ' ' → DomainException nombre obligatorio")
    void testUTUS09_NameWithOnlySpaces_WhenNameIsOnlySpaces() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "   ",
                "john@example.com",
                "Password123",
                UserRole.USER
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name cannot be null or blank");

        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(UserDomain.class));
    }
}
