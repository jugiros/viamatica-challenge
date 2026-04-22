package com.viamatica.assessment.orders_management_system.domain.order;

/**
 * Represents an order that has been confirmed but not yet paid.
 * Can transition to PAGADA or CANCELADA.
 */
public final record ConfirmedStatus() implements OrderStatus {

    @Override
    public boolean canTransitionTo(OrderStatus next) {
        return next instanceof PaidStatus || next instanceof CancelledStatus;
    }

    @Override
    public String name() {
        return "CONFIRMADA";
    }
}
