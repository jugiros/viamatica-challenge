package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.exception.InvalidOrderStateTransitionException;
import com.viamatica.assessment.orders_management_system.domain.order.CancelledStatus;
import com.viamatica.assessment.orders_management_system.domain.order.OrderStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PendingStatus;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDomain {

    private Long id;
    private String orderNumber;
    private Long userId;
    private OrderStatus status;
    private Money total;
    private long version;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<OrderItemDomain> items;

    private OrderDomain() {
    }

    private OrderDomain(Builder builder) {
        this.id = builder.id;
        this.orderNumber = builder.orderNumber != null ? builder.orderNumber : generateOrderNumber();
        this.userId = builder.userId;
        this.status = builder.status != null ? builder.status : new PendingStatus();
        this.total = builder.total;
        this.version = builder.version;
        this.orderDate = builder.orderDate != null ? builder.orderDate : LocalDateTime.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : LocalDateTime.now();
        this.deletedAt = builder.deletedAt;
        this.items = builder.items != null ? new ArrayList<>(builder.items) : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Generates a unique order number.
     * @return unique order number
     */
    private static String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Override getItems to return unmodifiable list
     */
    public List<OrderItemDomain> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Override setItems to create defensive copy
     */
    public void setItems(List<OrderItemDomain> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    /**
     * Adds an item to this order.
     * @param item the order item to add
     */
    public void addItem(OrderItemDomain item) {
        this.items.add(item);
        recalculateTotal();
    }

    /**
     * Removes an item from this order.
     * @param item the order item to remove
     */
    public void removeItem(OrderItemDomain item) {
        this.items.remove(item);
        recalculateTotal();
    }

    /**
     * Recalculates the total based on all order items.
     */
    public void recalculateTotal() {
        Money newTotal = items.stream()
                .map(OrderItemDomain::getSubtotal)
                .reduce(Money.of(java.math.BigDecimal.ZERO), Money::add);
        this.total = newTotal;
    }

    /**
     * Transitions the order to a new status if valid.
     * @param newStatus the target status
     * @throws InvalidOrderStateTransitionException if transition is invalid
     */
    public void transitionTo(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStateTransitionException(
                    this.status.name(),
                    newStatus.name());
        }
        this.status = newStatus;
        this.version++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the order can be cancelled.
     * @return true if the order can be cancelled
     */
    public boolean canBeCancelled() {
        return this.status.canTransitionTo(new CancelledStatus());
    }

    /**
     * Cancels the order if possible.
     */
    public void cancel() {
        transitionTo(new CancelledStatus());
    }

    /**
     * Soft deletes this order by setting the deletedAt timestamp.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restores a soft-deleted order by clearing the deletedAt timestamp.
     */
    public void restore() {
        this.deletedAt = null;
    }

    /**
     * Updates the updatedAt timestamp to current time.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Builder pattern for OrderDomain.
     */
    public static class Builder {
        private Long id;
        private String orderNumber;
        private Long userId;
        private OrderStatus status;
        private Money total;
        private long version = 0;
        private LocalDateTime orderDate;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;
        private List<OrderItemDomain> items;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder total(Money total) {
            this.total = total;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder deletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public Builder items(List<OrderItemDomain> items) {
            this.items = items;
            return this;
        }

        public Builder addItem(OrderItemDomain item) {
            if (this.items == null) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);
            return this;
        }

        public OrderDomain build() {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            return new OrderDomain(this);
        }
    }
}
