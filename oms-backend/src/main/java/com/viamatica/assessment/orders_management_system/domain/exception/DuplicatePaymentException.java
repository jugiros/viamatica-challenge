package com.viamatica.assessment.orders_management_system.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when attempting to create a payment for an order that already has a payment.
 */
@Getter
public class DuplicatePaymentException extends DomainException {

    private final Long orderId;

    public DuplicatePaymentException(Long orderId) {
        super("Payment already exists for order ID: " + orderId);
        this.orderId = orderId;
    }

}
