package com.viamatica.assessment.orders_management_system.domain.exception;

import lombok.Getter;

@Getter
public class DuplicatePaymentException extends DomainException {

    private final Long orderId;

    public DuplicatePaymentException(Long orderId) {
        super("Payment already exists for order ID: " + orderId);
        this.orderId = orderId;
    }

}
