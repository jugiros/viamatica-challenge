package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        String method,
        String status,
        BigDecimal amount,
        String externalReference,
        LocalDateTime paymentDate
) {}
