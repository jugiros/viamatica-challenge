package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {}
