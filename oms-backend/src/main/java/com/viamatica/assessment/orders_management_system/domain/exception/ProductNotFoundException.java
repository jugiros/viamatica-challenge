package com.viamatica.assessment.orders_management_system.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested product cannot be found.
 */
@Getter
public class ProductNotFoundException extends DomainException {

    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("Product not found with ID: " + productId);
        this.productId = productId;
    }

}
