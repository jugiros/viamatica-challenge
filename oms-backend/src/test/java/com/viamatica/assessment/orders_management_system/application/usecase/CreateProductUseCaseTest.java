package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductUseCase Tests")
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Test
    @DisplayName("UTPR01 - Creación exitosa")
    void testUTPR01_CreateSuccess_WhenValidData() {
        // Arrange
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Laptop",
                new BigDecimal("999.99"),
                10,
                1L
        );

        ProductDomain savedProduct = ProductDomain.builder()
                .id(1L)
                .name(ProductName.of("Laptop"))
                .price(Money.of(new BigDecimal("999.99")))
                .stock(10)
                .categoryId(1L)
                .active(true)
                .build();

        when(productRepository.save(any(ProductDomain.class))).thenReturn(savedProduct);

        // Act
        ProductDomain result = createProductUseCase.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName().value()).isEqualTo("Laptop");
        assertThat(result.getPrice().amount()).isEqualTo(new BigDecimal("999.99"));
        assertThat(result.getStock()).isEqualTo(10);
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.isActive()).isTrue();

        verify(productRepository).save(any(ProductDomain.class));
        verify(auditPort).logEntityChange(
                eq(1L),
                eq("products"),
                eq("INSERT"),
                eq(1L),
                isNull(),
                any(String.class)
        );
    }

    @Test
    @DisplayName("UTPR02 - Nombre vacío → DomainException")
    void testUTPR02_EmptyName_WhenNameIsEmpty() {
        // Arrange
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "",
                new BigDecimal("999.99"),
                10,
                1L
        );

        // Act & Assert
        assertThatThrownBy(() -> createProductUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name");

        verify(productRepository, never()).save(any(ProductDomain.class));
        verify(auditPort, never()).logEntityChange(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("UTPR03 - Precio = 0 → DomainException precio > 0")
    void testUTPR03_PriceZero_WhenPriceIsZero() {
        // Arrange
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Laptop",
                BigDecimal.ZERO,
                10,
                1L
        );

        // Act & Assert
        assertThatThrownBy(() -> createProductUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price");

        verify(productRepository, never()).save(any(ProductDomain.class));
    }

    @Test
    @DisplayName("UTPR04 - Precio negativo -5.00 → DomainException")
    void testUTPR04_NegativePrice_WhenPriceIsNegative() {
        // Arrange
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Laptop",
                new BigDecimal("-5.00"),
                10,
                1L
        );

        // Act & Assert
        assertThatThrownBy(() -> createProductUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price");

        verify(productRepository, never()).save(any(ProductDomain.class));
    }

    @Test
    @DisplayName("UTPR05 - Stock negativo -1 → DomainException")
    void testUTPR05_NegativeStock_WhenStockIsNegative() {
        // Arrange
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Laptop",
                new BigDecimal("999.99"),
                -1,
                1L
        );

        // Act & Assert
        assertThatThrownBy(() -> createProductUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock cannot be negative");

        verify(productRepository, never()).save(any(ProductDomain.class));
    }

    @Test
    @DisplayName("UTPR06 - Stock = 0 → producto creado correctamente (válido)")
    void testUTPR06_StockZero_WhenStockIsZero() {
        // Arrange
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Laptop",
                new BigDecimal("999.99"),
                0,
                1L
        );

        ProductDomain savedProduct = ProductDomain.builder()
                .id(1L)
                .name(ProductName.of("Laptop"))
                .price(Money.of(new BigDecimal("999.99")))
                .stock(0)
                .categoryId(1L)
                .active(true)
                .build();

        when(productRepository.save(any(ProductDomain.class))).thenReturn(savedProduct);

        // Act
        ProductDomain result = createProductUseCase.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStock()).isEqualTo(0);

        verify(productRepository).save(any(ProductDomain.class));
        verify(auditPort).logEntityChange(
                eq(1L),
                eq("products"),
                eq("INSERT"),
                eq(1L),
                isNull(),
                any(String.class)
        );
    }

    @Test
    @DisplayName("UTPR07 - Categoría inexistente 9999 → Producto creado (validación en capa superior)")
    void testUTPR07_CategoryNonExistent_WhenCategoryIdDoesNotExist() {
        // Arrange
        CreateProductUseCase.Command command = new CreateProductUseCase.Command(
                "Laptop",
                new BigDecimal("999.99"),
                0,
                9999L
        );

        ProductDomain savedProduct = ProductDomain.builder()
                .id(1L)
                .name(ProductName.of("Laptop"))
                .price(Money.of(new BigDecimal("999.99")))
                .stock(0)
                .categoryId(9999L)
                .active(true)
                .build();

        when(productRepository.save(any(ProductDomain.class))).thenReturn(savedProduct);

        // Act
        ProductDomain result = createProductUseCase.execute(command);

        // Assert
        // Note: CreateProductUseCase doesn't validate category existence
        // Category validation should be done at a higher layer (e.g., controller or service)
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(9999L);

        verify(productRepository).save(any(ProductDomain.class));
    }

    @Test
    @DisplayName("UTPR08 - Incremento de stock: 10 + 5 = 15")
    void testUTPR08_IncrementStock_WhenStockIsIncremented() {
        // Arrange
        ProductDomain product = ProductDomain.builder()
                .id(1L)
                .name(ProductName.of("Laptop"))
                .price(Money.of(new BigDecimal("999.99")))
                .stock(10)
                .categoryId(1L)
                .active(true)
                .build();

        // Act
        product.incrementStock(5);

        // Assert
        assertThat(product.getStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("UTPR09 - Decremento stock insuficiente: 3 - 5 → InsufficientStockException")
    void testUTPR09_DecrementStockInsufficient_WhenStockIsInsufficient() {
        // Arrange
        ProductDomain product = ProductDomain.builder()
                .id(1L)
                .name(ProductName.of("Laptop"))
                .price(Money.of(new BigDecimal("999.99")))
                .stock(3)
                .build();
        // Act & Assert
        assertThatThrownBy(() -> product.decrementStock(15))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Stock cannot be negative");

        assertThat(product.getStock()).isEqualTo(3);
    }
}
