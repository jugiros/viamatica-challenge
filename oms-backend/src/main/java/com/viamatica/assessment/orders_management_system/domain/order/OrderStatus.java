package com.viamatica.assessment.orders_management_system.domain.order;

/**
 * Sealed interface representing the state machine for order statuses.
 * Implements the State pattern with valid transitions between states.
 *
 * Valid transitions:
 * PENDIENTE → CONFIRMADA, CANCELADA
 * CONFIRMADA → PAGADA, CANCELADA
 * PAGADA → ENVIADA
 * ENVIADA → (none)
 * CANCELADA → (none)
 */
public sealed interface OrderStatus permits
        PendingStatus,
        ConfirmedStatus,
        PaidStatus,
        ShippedStatus,
        CancelledStatus {

    /**
     * Checks if a transition to the given next status is valid.
     * @param next the target status
     * @return true if the transition is valid, false otherwise
     */
    boolean canTransitionTo(OrderStatus next);

    /**
     * Returns the name of this status.
     * @return the status name
     */
    String name();

    /**
     * Checks if this status represents a final state (no further transitions allowed).
     * @return true if this is a final state
     */
    default boolean isFinal() {
        return this instanceof ShippedStatus || this instanceof CancelledStatus;
    }

    /**
     * Checks if this status represents a cancelled order.
     * @return true if the order is cancelled
     */
    default boolean isCancelled() {
        return this instanceof CancelledStatus;
    }

    /**
     * Checks if this status represents a paid order.
     * @return true if the order is paid
     */
    default boolean isPaid() {
        return this instanceof PaidStatus || this instanceof ShippedStatus;
    }
}
