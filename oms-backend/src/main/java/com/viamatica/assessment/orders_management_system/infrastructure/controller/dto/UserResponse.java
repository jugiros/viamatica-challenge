package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
