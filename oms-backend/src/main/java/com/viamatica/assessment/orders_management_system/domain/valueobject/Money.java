package com.viamatica.assessment.orders_management_system.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public static Money of(BigDecimal amount) {
        return new Money(amount.setScale(SCALE, ROUNDING));
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount).setScale(SCALE, ROUNDING));
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount).setScale(SCALE, ROUNDING));
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
