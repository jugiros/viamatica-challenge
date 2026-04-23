package com.viamatica.assessment.orders_management_system.domain.order;

public sealed interface OrderStatus permits
        PendingStatus,
        ConfirmedStatus,
        PaidStatus,
        ShippedStatus,
        CancelledStatus {

    boolean canTransitionTo(OrderStatus next);

    String name();

    default boolean isFinal() {
        return this instanceof ShippedStatus || this instanceof CancelledStatus;
    }

    default boolean isCancelled() {
        return this instanceof CancelledStatus;
    }

    default boolean isPaid() {
        return this instanceof PaidStatus || this instanceof ShippedStatus;
    }
}
