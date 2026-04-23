package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer cantidad,
        BigDecimal subtotal
) {}
