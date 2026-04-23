package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String name,
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 8, max = 100)
        String password,
        String role
) {}
