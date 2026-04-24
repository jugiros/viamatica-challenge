package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        String name,
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {}
