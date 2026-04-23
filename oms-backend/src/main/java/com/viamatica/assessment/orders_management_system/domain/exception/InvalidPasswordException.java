package com.viamatica.assessment.orders_management_system.domain.exception;

public class InvalidPasswordException extends DomainException {

    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
