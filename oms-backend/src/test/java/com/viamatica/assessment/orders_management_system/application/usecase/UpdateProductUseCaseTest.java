package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.ProductNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuditPort auditPort;

    private UpdateProductUseCase updateProductUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        updateProductUseCase = new UpdateProductUseCase(productRepository, auditPort);
    }

    @Test
    void execute_ValidUpdate_ShouldReturnUpdatedProduct() {
        ProductDomain existingProduct = ProductDomain.builder()
            .id(1L)
            .name(com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName.of("Old Product"))
            .price(Money.of(BigDecimal.valueOf(100.0)))
            .stock(10)
            .categoryId(1L)
            .active(true)
            .createdAt(LocalDateTime.now())
            .build();

        ProductDomain updatedProduct = ProductDomain.builder()
            .id(1L)
            .name(com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName.of("New Product"))
            .price(Money.of(BigDecimal.valueOf(150.0)))
            .stock(20)
            .categoryId(1L)
            .active(true)
            .createdAt(existingProduct.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any())).thenReturn(updatedProduct);

        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
            1L,
            "New Product",
            BigDecimal.valueOf(150.0),
            20,
            null,
            null
        );

        ProductDomain result = updateProductUseCase.execute(command);

        assertNotNull(result);
        assertEquals("New Product", result.getName().value());
        verify(productRepository).save(any());
        verify(auditPort).logEntityChange(any(), eq("products"), eq("UPDATE"), any(), any(), any());
    }

    @Test
    void execute_ProductNotFound_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateProductUseCase.Command command = new UpdateProductUseCase.Command(
            1L,
            "New Product",
            BigDecimal.valueOf(150.0),
            20,
            null,
            null
        );

        assertThrows(ProductNotFoundException.class, () -> updateProductUseCase.execute(command));
    }
}
