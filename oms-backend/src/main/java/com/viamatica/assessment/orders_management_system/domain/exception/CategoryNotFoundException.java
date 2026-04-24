package com.viamatica.assessment.orders_management_system.domain.exception;

import lombok.Getter;

@Getter
public class CategoryNotFoundException extends DomainException {

    private final Long categoryId;

    public CategoryNotFoundException(Long categoryId) {
        super("Category not found with ID: " + categoryId);
        this.categoryId = categoryId;
    }

}
