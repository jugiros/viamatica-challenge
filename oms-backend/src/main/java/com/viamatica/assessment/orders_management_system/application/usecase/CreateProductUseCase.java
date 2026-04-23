package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final AuditPort auditPort;

    public record Command(
            String name,
            BigDecimal price,
            int stock,
            Long categoryId
    ) {}

    public ProductDomain execute(Command command) {
        if (command.price == null || command.price.signum() == 0) {
            throw new IllegalArgumentException("Price cannot be null or zero.");
        }

        if (command.stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        if (command.categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        ProductName productName = ProductName.of(command.name);
        if (productRepository.findByName(productName).isPresent()) {
            throw new com.viamatica.assessment.orders_management_system.domain.exception.DomainException("Product with name '" + command.name + "' already exists.");
        }

        ProductDomain product = ProductDomain.builder()
                .name(productName)
                .price(Money.of(command.price))
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
                "{\"id\":" + savedProduct.getId() + ",\"name\":\"" + savedProduct.getName().value() + "\"}"
        );

        return savedProduct;
    }
}
