package com.viamatica.assessment.orders_management_system.domain.order;

/**
 * Represents an order that has been shipped.
 * This is a final state with no further transitions allowed.
 */
public final record ShippedStatus() implements OrderStatus {

    @Override
    public boolean canTransitionTo(OrderStatus next) {
        return false;
    }

    @Override
    public String name() {
        return "ENVIADA";
    }
}
