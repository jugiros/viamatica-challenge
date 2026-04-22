package com.viamatica.assessment.orders_management_system.domain.order;

/**
 * Represents an order that has been paid but not yet shipped.
 * Can transition only to ENVIADA.
 */
public final record PaidStatus() implements OrderStatus {

    @Override
    public boolean canTransitionTo(OrderStatus next) {
        return next instanceof ShippedStatus;
    }

    @Override
    public String name() {
        return "PAGADA";
    }
}
