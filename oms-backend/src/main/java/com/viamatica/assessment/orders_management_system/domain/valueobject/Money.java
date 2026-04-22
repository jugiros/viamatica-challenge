package com.viamatica.assessment.orders_management_system.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object representing a monetary amount with validation.
 * Ensures the amount is positive and has exactly 2 decimal places.
 */
public record Money(BigDecimal amount) {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (amount.scale() > SCALE) {
            throw new IllegalArgumentException("Amount cannot have more than 2 decimal places");
        }
    }

    /**
     * Creates a Money instance from a decimal value.
     * @param amount the monetary amount
     * @return Money instance
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount.setScale(SCALE, ROUNDING));
    }

    /**
     * Creates a Money instance from a double value.
     * @param amount the monetary amount
     * @return Money instance
     */
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount).setScale(SCALE, ROUNDING));
    }

    /**
     * Creates a Money instance from a string value.
     * @param amount the monetary amount as string
     * @return Money instance
     */
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount).setScale(SCALE, ROUNDING));
    }

    /**
     * Adds another Money instance to this one.
     * @param other the other Money instance
     * @return new Money instance with the sum
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount).setScale(SCALE, ROUNDING));
    }

    /**
     * Multiplies this Money instance by a factor.
     * @param multiplier the multiplication factor
     * @return new Money instance with the product
     */
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier).setScale(SCALE, ROUNDING));
    }

    /**
     * Multiplies this Money instance by a factor.
     * @param multiplier the multiplication factor
     * @return new Money instance with the product
     */
    public Money multiply(int multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
