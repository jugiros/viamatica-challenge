package com.viamatica.assessment.orders_management_system.domain.exception;

/**
 * Exception thrown when a requested product cannot be found.
 */
public class ProductNotFoundException extends DomainException {

    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("Product not found with ID: " + productId);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
