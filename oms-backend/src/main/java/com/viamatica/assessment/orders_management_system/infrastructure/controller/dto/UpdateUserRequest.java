package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 50)
        String name,
        String role,
        Boolean active
) {}
