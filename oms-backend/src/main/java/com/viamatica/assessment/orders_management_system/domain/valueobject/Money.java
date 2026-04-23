package com.viamatica.assessment.orders_management_system.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount) {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        if (amount.scale() > SCALE) {
            throw new IllegalArgumentException("Price cannot have more than 2 decimal places");
        }
    }

    public static Money of(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return new Money(amount.setScale(SCALE, ROUNDING));
    }

    public static Money of(double amount) {
        BigDecimal value = BigDecimal.valueOf(amount);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return new Money(value.setScale(SCALE, ROUNDING));
    }

    public static Money of(String amount) {
        BigDecimal value = new BigDecimal(amount);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return new Money(value.setScale(SCALE, ROUNDING));
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount).setScale(SCALE, ROUNDING));
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier).setScale(SCALE, ROUNDING));
    }

    public Money multiply(int multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
