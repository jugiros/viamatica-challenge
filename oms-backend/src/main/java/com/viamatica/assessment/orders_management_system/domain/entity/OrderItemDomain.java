package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import lombok.Data;

@Data
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

    /**
     * Override setQuantity to recalculate subtotal
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }

    /**
     * Override setUnitPrice to recalculate subtotal
     */
    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    /**
     * Recalculates the subtotal based on current quantity and unit price.
     */
    private void recalculateSubtotal() {
        this.subtotal = calculateSubtotal(this.quantity, this.unitPrice);
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

            this.subtotal = subtotal;
            return this;
        }

        public OrderItemDomain build() {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null) {
                throw new IllegalArgumentException("Unit price cannot be null");
            }
            return new OrderItemDomain(this);
{{ ... }}
    }
}
