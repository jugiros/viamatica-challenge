package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Use case for creating a new product.
 * Validates ProductName, Money, stock >= 0, and category exists.
 */
@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final AuditPort auditPort;

    public CreateProductUseCase(ProductRepository productRepository, AuditPort auditPort) {
        this.productRepository = productRepository;
        this.auditPort = auditPort;
    }

    public record Command(
            String name,
            String description,
            BigDecimal price,
            int stock,
            Long categoryId
    ) {}

    public ProductDomain execute(Command command) {
        ProductName productName = ProductName.of(command.name);
        Money price = Money.of(command.price);

        if (command.stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        if (command.categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        ProductDomain product = ProductDomain.builder()
                .name(productName)
                .description(command.description)
                .price(price)
                .stock(command.stock)
                .categoryId(command.categoryId)
                .active(true)
                .build();

        ProductDomain savedProduct = productRepository.save(product);

        auditPort.logEntityChange(
                savedProduct.getId(),
                "products",
                "INSERT",
                savedProduct.getId(),
                null,
                "{\"id\":" + savedProduct.getId() + ",\"name\":\"" + productName.value() + "\"}"
        );

        return savedProduct;
    }
}
