package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        LocalDateTime createdAt
) {}
