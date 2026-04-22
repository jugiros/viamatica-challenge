package com.viamatica.assessment.orders_management_system.domain.exception;

/**
 * Exception thrown when a password validation fails.
 */
public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
