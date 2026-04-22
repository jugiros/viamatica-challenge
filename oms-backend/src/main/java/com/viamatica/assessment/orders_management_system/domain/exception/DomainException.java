package com.viamatica.assessment.orders_management_system.domain.exception;

/**
 * Base exception for all domain-specific exceptions.
 * Used to represent business rule violations and domain errors.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
