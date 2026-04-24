package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.valueobject.CategoryName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class CategoryDomain {

    private Long id;
    private CategoryName name;
    private String description;
    private LocalDateTime createdAt;
}
