package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
