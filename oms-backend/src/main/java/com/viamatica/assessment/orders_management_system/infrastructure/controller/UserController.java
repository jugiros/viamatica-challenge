package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.application.usecase.UpdateUserUseCase;
import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.UpdateUserRequest;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "User management endpoints (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;
    private final UpdateUserUseCase updateUserUseCase;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination")
    public ResponseEntity<Page<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<UserDomain> users = userRepository.findAllActive();
        
        List<UserResponse> responses = users.stream()
                .map(this::toUserResponse)
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        
        Page<UserResponse> pageResponse = new PageImpl<>(
                responses.subList(start, end),
                pageable,
                responses.size()
        );
        
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        UserDomain user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserUseCase.Command command = new UpdateUserUseCase.Command(
                id,
                request.name(),
                request.role() != null ? 
                        com.viamatica.assessment.orders_management_system.domain.model.UserRole.valueOf(request.role()) 
                        : null,
                request.active()
        );
        UserDomain user = updateUserUseCase.execute(command);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Soft delete a user by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserDomain user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.deactivate();
        userRepository.save(user);
        return ResponseEntity.noContent().build();
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
