package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentStatus;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDomain {

    private Long id;
    private Long orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private Money amount;
    private String externalReference;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PaymentDomain() {
    }

    private PaymentDomain(Builder builder) {
        this.id = builder.id;
        this.orderId = builder.orderId;
        this.method = builder.method;
        this.status = builder.status != null ? builder.status : PaymentStatus.PENDING;
        this.amount = builder.amount;
        this.externalReference = builder.externalReference;
        this.paymentDate = builder.paymentDate != null ? builder.paymentDate : LocalDateTime.now();
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Marks this payment as completed.
     */
    public void complete() {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks this payment as failed.
     */
    public void fail() {
        this.status = PaymentStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marks this payment as refunded.
     */
    public void refund() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }
        this.status = PaymentStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if this payment is successful.
     * @return true if payment is completed
     */
    public boolean isSuccessful() {
        return this.status == PaymentStatus.COMPLETED;
    }

    /**
     * Checks if this payment is pending.
     * @return true if payment is pending
     */
    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    /**
     * Updates the updatedAt timestamp to current time.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Builder pattern for PaymentDomain.
     */
    public static class Builder {
        private Long id;
        private Long orderId;
        private PaymentMethod method;
        private PaymentStatus status;
        private Money amount;
        private String externalReference;
        private LocalDateTime paymentDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder method(PaymentMethod method) {
            this.method = method;
            return this;
        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder externalReference(String externalReference) {
            this.externalReference = externalReference;
            return this;
        }

        public Builder paymentDate(LocalDateTime paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PaymentDomain build() {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            if (method == null) {
                throw new IllegalArgumentException("Payment method cannot be null");
            }
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
            return new PaymentDomain(this);
        }
    }
}
