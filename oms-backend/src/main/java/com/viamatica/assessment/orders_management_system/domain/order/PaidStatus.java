package com.viamatica.assessment.orders_management_system.domain.order;

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
