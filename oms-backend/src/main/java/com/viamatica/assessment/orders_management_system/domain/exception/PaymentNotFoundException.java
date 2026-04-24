package com.viamatica.assessment.orders_management_system.domain.exception;

import lombok.Getter;

@Getter
public class PaymentNotFoundException extends DomainException {

    private final Long paymentId;

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with ID: " + paymentId);
        this.paymentId = paymentId;
    }

    public PaymentNotFoundException(String message) {
        super(message);
        this.paymentId = null;
    }

}
