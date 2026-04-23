package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductDomain {

    private Long id;
    private ProductName name;
    private Money price;
    private int stock;
    private Long categoryId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean hasSufficientStock(int quantity) {
        return stock >= quantity;
    }

    public void incrementStock(int amount) {
        this.stock += amount;
        touch();
    }

    public void decrementStock(int amount) {
        if (stock < amount) {
            throw new IllegalStateException("Stock cannot be negative");
        }
        this.stock -= amount;
        touch();
    }

    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
