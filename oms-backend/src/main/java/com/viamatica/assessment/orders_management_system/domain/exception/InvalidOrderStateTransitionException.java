package com.viamatica.assessment.orders_management_system.domain.exception;

/**
 * Exception thrown when attempting to transition an order to an invalid state.
 */
public class InvalidOrderStateTransitionException extends DomainException {

    private final String fromStatus;
    private final String toStatus;

    public InvalidOrderStateTransitionException(String fromStatus, String toStatus) {
        super(String.format("Invalid order state transition from %s to %s", fromStatus, toStatus));
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }
}
