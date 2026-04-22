package com.viamatica.assessment.orders_management_system.domain.valueobject;

/**
 * Value Object representing a validated product name.
 * Ensures the name is not empty and does not exceed 200 characters.
 */
public record ProductName(String value) {

    private static final int MAX_LENGTH = 200;

    public ProductName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Product name cannot exceed " + MAX_LENGTH + " characters");
        }
    }

    /**
     * Creates a ProductName instance from a string.
     * @param name the product name
     * @return ProductName instance
     */
    public static ProductName of(String name) {
        return new ProductName(name);
    }

    /**
     * Returns the trimmed value of the product name.
     * @return trimmed product name
     */
    public String trimmed() {
        return value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
