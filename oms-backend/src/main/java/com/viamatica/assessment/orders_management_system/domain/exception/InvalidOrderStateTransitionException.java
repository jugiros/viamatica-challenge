package com.viamatica.assessment.orders_management_system.domain.exception;

import lombok.Getter;

@Getter
public class InvalidOrderStateTransitionException extends DomainException {

    private final String fromStatus;
    private final String toStatus;

    public InvalidOrderStateTransitionException(String fromStatus, String toStatus) {
        super(String.format("Invalid order state transition from %s to %s", fromStatus, toStatus));
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

}
