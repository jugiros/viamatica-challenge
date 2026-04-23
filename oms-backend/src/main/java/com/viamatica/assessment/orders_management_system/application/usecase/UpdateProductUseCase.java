package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.ProductNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final AuditPort auditPort;

    public record Command(
            Long id,
            String name,
            BigDecimal price,
            Integer stock,
            Long categoryId,
            Boolean active
    ) {}

    public ProductDomain execute(Command command) {
        ProductDomain product = productRepository.findById(command.id)
                .orElseThrow(() -> new ProductNotFoundException(command.id));

        String previousValues = "{\"id\":" + product.getId() + ",\"name\":\"" + product.getName() + "\",\"price\":" + product.getPrice() + ",\"stock\":" + product.getStock() + ",\"active\":" + product.isActive() + "}";

        ProductDomain updatedProduct = ProductDomain.builder()
                .id(product.getId())
                .name(command.name != null ? ProductName.of(command.name) : product.getName())
                .price(command.price != null ? Money.of(command.price) : product.getPrice())
                .stock(command.stock != null ? command.stock : product.getStock())
                .categoryId(command.categoryId != null ? command.categoryId : product.getCategoryId())
                .active(command.active != null ? command.active : product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductDomain savedProduct = productRepository.save(updatedProduct);

        String newValues = "{\"id\":" + savedProduct.getId() + ",\"name\":\"" + savedProduct.getName() + "\",\"price\":" + savedProduct.getPrice() + ",\"stock\":" + savedProduct.getStock() + ",\"active\":" + savedProduct.isActive() + "}";

        auditPort.logEntityChange(
                savedProduct.getId(),
                "products",
                "UPDATE",
                savedProduct.getId(),
                previousValues,
                newValues
        );

        return savedProduct;
    }
}
