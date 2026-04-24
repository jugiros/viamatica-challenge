package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProcessPaymentRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,
        @NotNull(message = "Payment method is required")
        PaymentMethod method,
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        String externalReference
) {}
