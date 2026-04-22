package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import java.util.Objects;

/**
 * Domain entity representing an item within an order.
 * Pure POJO without any framework annotations.
 */
public class OrderItemDomain {

    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private Money unitPrice;
    private Money subtotal;

    private OrderItemDomain() {
    }

    private OrderItemDomain(Builder builder) {
        this.id = builder.id;
        this.orderId = builder.orderId;
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.unitPrice = builder.unitPrice;
        this.subtotal = builder.subtotal != null ? builder.subtotal : calculateSubtotal(builder.quantity, builder.unitPrice);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Calculates the subtotal for this order item.
     * @param quantity the quantity
     * @param unitPrice the unit price
     * @return the subtotal
     */
    private static Money calculateSubtotal(int quantity, Money unitPrice) {
        return unitPrice.multiply(quantity);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money getSubtotal() {
        return subtotal;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public void setSubtotal(Money subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Recalculates the subtotal based on current quantity and unit price.
     */
    private void recalculateSubtotal() {
        this.subtotal = calculateSubtotal(this.quantity, this.unitPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDomain that = (OrderItemDomain) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, productId);
    }

    @Override
    public String toString() {
        return "OrderItemDomain{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }

    /**
     * Builder pattern for OrderItemDomain.
     */
    public static class Builder {
        private Long id;
        private Long orderId;
        private Long productId;
        private int quantity;
        private Money unitPrice;
        private Money subtotal;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder orderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder unitPrice(Money unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder subtotal(Money subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public OrderItemDomain build() {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            if (unitPrice == null) {
                throw new IllegalArgumentException("Unit price cannot be null");
            }
            return new OrderItemDomain(this);
        }
    }
}
