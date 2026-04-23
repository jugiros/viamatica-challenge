package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.application.usecase.CreateProductUseCase;
import com.viamatica.assessment.orders_management_system.application.usecase.UpdateProductUseCase;
import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.ProductNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.CreateProductRequest;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.ProductResponse;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Product management endpoints")
public class ProductController {

    private final ProductRepository productRepository;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve all products with pagination (public)")
    public ResponseEntity<Page<ProductResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ProductDomain> products = productRepository.findAllActive();
        
        List<ProductResponse> responses = products.stream()
                .map(this::toProductResponse)
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        
        Page<ProductResponse> pageResponse = new PageImpl<>(
                responses.subList(start, end),
                pageable,
                responses.size()
        );
        
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by ID (public)")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        ProductDomain product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(toProductResponse(product));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.categoryId()
        );
        ProductDomain product = createProductUseCase.execute(command);
        return ResponseEntity.ok(toProductResponse(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product", description = "Update product information (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.categoryId(),
                request.active()
        );
        ProductDomain product = updateProductUseCase.execute(command);
        return ResponseEntity.ok(toProductResponse(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product", description = "Soft delete a product by ID (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ProductDomain product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.softDelete();
        productRepository.save(product);
        return ResponseEntity.noContent().build();
    }

    private ProductResponse toProductResponse(ProductDomain product) {
        return new ProductResponse(
                product.getId(),
                product.getName().value(),
                product.getDescription(),
                product.getPrice().amount(),
                product.getStock(),
                product.getCategoryId(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
