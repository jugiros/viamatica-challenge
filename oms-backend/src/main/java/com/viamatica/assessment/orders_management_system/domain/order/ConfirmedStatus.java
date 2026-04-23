package com.viamatica.assessment.orders_management_system.domain.order;

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
