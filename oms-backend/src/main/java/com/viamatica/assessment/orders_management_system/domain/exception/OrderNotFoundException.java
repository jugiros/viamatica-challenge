package com.viamatica.assessment.orders_management_system.domain.exception;

/**
 * Exception thrown when a requested order cannot be found.
 */
public class OrderNotFoundException extends DomainException {

    private final Long id;
    private final String orderNumber;

    public OrderNotFoundException(Long id) {
        super("Order not found with ID: " + id);
        this.id = id;
        this.orderNumber = null;
    }

    public OrderNotFoundException(String orderNumber) {
        super("Order not found with number: " + orderNumber);
        this.id = null;
        this.orderNumber = orderNumber;
    }

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }
}
