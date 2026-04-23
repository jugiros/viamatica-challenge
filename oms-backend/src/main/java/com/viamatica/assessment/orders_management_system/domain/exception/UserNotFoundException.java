package com.viamatica.assessment.orders_management_system.domain.exception;

import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import lombok.Getter;

/**
 * Exception thrown when a requested user cannot be found.
 */
@Getter
public class UserNotFoundException extends DomainException {

    private final Long id;
    private final Email email;

    public UserNotFoundException(Long id) {
        super("User not found with ID: " + id);
        this.id = id;
        this.email = null;
    }

    public UserNotFoundException(Email email) {
        super("User not found with email: " + email.value());
        this.id = null;
        this.email = email;
    }

}
