package com.viamatica.assessment.orders_management_system.domain.exception;

/**
 * Exception thrown when attempting to create a payment for an order that already has a payment.
 */
public class DuplicatePaymentException extends DomainException {

    private final Long orderId;

    public DuplicatePaymentException(Long orderId) {
        super("Payment already exists for order ID: " + orderId);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
