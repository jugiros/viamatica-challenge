package com.viamatica.assessment.orders_management_system.domain.order;

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
