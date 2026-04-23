package com.viamatica.assessment.orders_management_system.infrastructure.controller.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long id, Long productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
}
