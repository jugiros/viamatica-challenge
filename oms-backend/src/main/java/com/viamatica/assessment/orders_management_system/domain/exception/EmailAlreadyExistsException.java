package com.viamatica.assessment.orders_management_system.domain.exception;

import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;

public class EmailAlreadyExistsException extends DomainException {

    private final Email email;

    public EmailAlreadyExistsException(Email email) {
        super("Email already exists: " + email.value());
        this.email = email;
    }

    public Email getEmail() {
        return email;
    }
}
