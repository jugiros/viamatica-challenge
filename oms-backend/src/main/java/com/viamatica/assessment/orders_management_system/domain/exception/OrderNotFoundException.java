package com.viamatica.assessment.orders_management_system.domain.exception;

import lombok.Getter;

@Getter
public class OrderNotFoundException extends DomainException {

    private final Long id;

    public OrderNotFoundException(Long id) {
        super("Order not found with ID: " + id);
        this.id = id;
    }

}
