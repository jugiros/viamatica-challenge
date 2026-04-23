package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn
) {}
