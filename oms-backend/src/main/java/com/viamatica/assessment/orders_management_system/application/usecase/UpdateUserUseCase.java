package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final AuditPort auditPort;

    public record Command(
            Long id,
            String name,
            UserRole role,
            Boolean active
    ) {}

    public UserDomain execute(Command command) {
        UserDomain user = userRepository.findById(command.id)
                .orElseThrow(() -> new UserNotFoundException(command.id));

        String previousValues = "{\"id\":" + user.getId() + ",\"name\":\"" + user.getName() + "\",\"role\":\"" + user.getRole() + "\",\"active\":" + user.isActive() + "}";

        UserDomain updatedUser = UserDomain.builder()
                .id(user.getId())
                .name(command.name != null ? command.name : user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(command.role != null ? command.role : user.getRole())
                .active(command.active != null ? command.active : user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .deletedAt(user.getDeletedAt())
                .build();

        UserDomain savedUser = userRepository.save(updatedUser);

        String newValues = "{\"id\":" + savedUser.getId() + ",\"name\":\"" + savedUser.getName() + "\",\"role\":\"" + savedUser.getRole() + "\",\"active\":" + savedUser.isActive() + "}";

        auditPort.logEntityChange(
                savedUser.getId(),
                "users",
                "UPDATE",
                savedUser.getId(),
                previousValues,
                newValues
        );

        return savedUser;
    }
}
