package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.EmailAlreadyExistsException;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidPasswordException;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final AuditPort auditPort;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public record Command(
            String name,
            String email,
            String password,
            UserRole role
    ) {}

    public UserDomain execute(Command command) {
        Email email = Email.of(command.email);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        // Validate password before hashing
        validatePassword(command.password);

        String passwordHash = passwordEncoder.encode(command.password);

        UserDomain user = UserDomain.builder()
                .name(command.name)
                .email(email)
                .passwordHash(passwordHash)
                .role(command.role != null ? command.role : UserRole.USER)
                .active(true)
                .build();

        UserDomain savedUser = userRepository.save(user);

        auditPort.logEntityChange(
                savedUser.getId(),
                "users",
                "INSERT",
                savedUser.getId(),
                null,
                "{\"id\":" + savedUser.getId() + ",\"email\":\"" + email.value() + "\"}"
        );

        return savedUser;
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new InvalidPasswordException("Password must contain at least one number");
        }
    }
}
