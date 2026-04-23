package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        @Size(min = 3, max = 200)
        String name,
        @NotNull
        @DecimalMin("0.01")
        BigDecimal price,
        @NotNull
        @Min(0)
        Integer stock,
        @NotNull
        Long categoryId
) {}
