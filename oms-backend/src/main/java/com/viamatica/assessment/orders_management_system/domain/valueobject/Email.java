package com.viamatica.assessment.orders_management_system.domain.valueobject;

import java.util.regex.Pattern;

/**
 * Value Object representing a validated email address.
 * Uses Java 21 record with compact constructor for validation.
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    /**
     * Creates an Email instance from a string.
     * @param email the email string
     * @return Email instance
     */
    public static Email of(String email) {
        return new Email(email);
    }

    @Override
    public String toString() {
        return value;
    }
}
