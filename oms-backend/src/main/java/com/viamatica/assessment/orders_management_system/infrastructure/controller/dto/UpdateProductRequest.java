package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @Size(min = 3, max = 200)
        String name,
        @Size(max = 1000)
        String description,
        @DecimalMin("0.01")
        BigDecimal price,
        @Min(0)
        Integer stock,
        Long categoryId,
        Boolean active
) {}
