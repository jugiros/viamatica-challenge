package com.viamatica.assessment.orders_management_system.domain.valueobject;

public record ProductName(String value) {

    private static final int MAX_LENGTH = 200;

    public ProductName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Product Name cannot be null or blank");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Product Name cannot be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Product Name cannot exceed " + MAX_LENGTH + " characters");
        }
    }

    public static ProductName of(String name) {
        return new ProductName(name);
    }

    public String trimmed() {
        return value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
