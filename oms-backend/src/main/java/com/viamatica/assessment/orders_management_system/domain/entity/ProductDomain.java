package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDomain {

    private Long id;
    private ProductName name;
    private String description;
    private Money price;
    private int stock;
    private Long categoryId;
    private boolean active;
    private long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private ProductDomain() {
    }

    private ProductDomain(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.stock = builder.stock;
        this.categoryId = builder.categoryId;
        this.active = builder.active;
        this.version = builder.version;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.deletedAt = builder.deletedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Checks if the product has sufficient stock for the requested quantity.
     * @param quantity the requested quantity
     * @return true if stock is sufficient
     */
    public boolean hasSufficientStock(int quantity) {
        return stock >= quantity;
    }

    /**
     * Decrements the stock by the specified quantity.
     * @param quantity the quantity to decrement
     * @throws IllegalArgumentException if insufficient stock
     */
    public void decrementStock(int quantity) {
        if (!hasSufficientStock(quantity)) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + stock + ", Requested: " + quantity);
        }
        this.stock -= quantity;
        this.version++;
    }

    /**
     * Increments the stock by the specified quantity.
     * @param quantity the quantity to increment
     */
    public void incrementStock(int quantity) {
        this.stock += quantity;
        this.version++;
    }

    /**
     * Soft deletes this product by setting the deletedAt timestamp.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    /**
     * Restores a soft-deleted product by clearing the deletedAt timestamp.
     */
    public void restore() {
        this.deletedAt = null;
        this.active = true;
    }

    /**
     * Updates the updatedAt timestamp to current time.
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Builder pattern for ProductDomain.
     */
    public static class Builder {
        private Long id;
        private ProductName name;
        private String description;
        private Money price;
        private int stock = 0;
        private Long categoryId;
        private boolean active = true;
        private long version = 0;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();
        private LocalDateTime deletedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(ProductName name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder stock(int stock) {
            this.stock = stock;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
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

        public Builder deletedAt(LocalDateTime deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public ProductDomain build() {
            if (name == null) {
                throw new IllegalArgumentException("Name cannot be null");
            }
            if (price == null) {
                throw new IllegalArgumentException("Price cannot be null");
            }
            if (stock < 0) {
                throw new IllegalArgumentException("Stock cannot be negative");
            }
            if (categoryId == null) {
                throw new IllegalArgumentException("Category ID cannot be null");
            }
            return new ProductDomain(this);
        }
    }
}
