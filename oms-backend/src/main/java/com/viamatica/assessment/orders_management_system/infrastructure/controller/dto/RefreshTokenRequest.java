package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank
        String refreshToken
) {}
