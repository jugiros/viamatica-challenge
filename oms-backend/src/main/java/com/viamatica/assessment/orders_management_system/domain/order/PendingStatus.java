package com.viamatica.assessment.orders_management_system.domain.order;

public final record PendingStatus() implements OrderStatus {

    @Override
    public boolean canTransitionTo(OrderStatus next) {
        return next instanceof ConfirmedStatus || next instanceof CancelledStatus;
    }

    @Override
    public String name() {
        return "PENDIENTE";
    }
}
