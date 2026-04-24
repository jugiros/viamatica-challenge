package com.viamatica.assessment.orders_management_system.domain.valueobject;

public record CategoryName(String value) {

    private static final int MAX_LENGTH = 100;

    public CategoryName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Category Name cannot be null or blank");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Category Name cannot be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Category Name cannot exceed " + MAX_LENGTH + " characters");
        }
    }

    public static CategoryName of(String name) {
        return new CategoryName(name);
    }

    public String trimmed() {
        return value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
