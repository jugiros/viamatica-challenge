package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.InsufficientStockException;
import com.viamatica.assessment.orders_management_system.domain.exception.ProductNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateOrderUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuditPort auditPort;

    private CreateOrderUseCase createOrderUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        createOrderUseCase = new CreateOrderUseCase(userRepository, productRepository, orderRepository, auditPort);
    }

    @Test
    void execute_ValidOrder_ShouldReturnOrder() {
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(true)
            .build();

        ProductDomain product = ProductDomain.builder()
            .id(1L)
            .name(com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName.of("Test Product"))
            .price(Money.of(BigDecimal.valueOf(100.0)))
            .stock(10)
            .active(true)
            .build();

        OrderDomain savedOrder = OrderDomain.builder()
            .id(1L)
            .userId(1L)
            .total(Money.of(BigDecimal.valueOf(100.0)))
            .items(List.of())
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenReturn(savedOrder);

        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(
            1L,
            List.of(new CreateOrderUseCase.OrderItemCommand(1L, 1))
        );

        OrderDomain result = createOrderUseCase.execute(command);

        assertNotNull(result);
        verify(orderRepository).save(any());
        verify(auditPort).logEntityChange(any(), eq("orders"), eq("INSERT"), any(), any(), any());
    }

    @Test
    void execute_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(
            1L,
            List.of(new CreateOrderUseCase.OrderItemCommand(1L, 1))
        );

        assertThrows(UserNotFoundException.class, () -> createOrderUseCase.execute(command));
    }

    @Test
    void execute_InactiveUser_ShouldThrowException() {
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(false)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(
            1L,
            List.of(new CreateOrderUseCase.OrderItemCommand(1L, 1))
        );

        assertThrows(IllegalArgumentException.class, () -> createOrderUseCase.execute(command));
    }

    @Test
    void execute_ProductNotFound_ShouldThrowException() {
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(true)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(
            1L,
            List.of(new CreateOrderUseCase.OrderItemCommand(1L, 1))
        );

        assertThrows(ProductNotFoundException.class, () -> createOrderUseCase.execute(command));
    }

    @Test
    void execute_InsufficientStock_ShouldThrowException() {
        UserDomain user = UserDomain.builder()
            .id(1L)
            .name("Test User")
            .email(com.viamatica.assessment.orders_management_system.domain.valueobject.Email.of("test@example.com"))
            .passwordHash("hashed-password")
            .role(com.viamatica.assessment.orders_management_system.domain.model.UserRole.USER)
            .active(true)
            .build();

        ProductDomain product = ProductDomain.builder()
            .id(1L)
            .name(com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName.of("Test Product"))
            .price(Money.of(BigDecimal.valueOf(100.0)))
            .stock(5)
            .active(true)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(
            1L,
            List.of(new CreateOrderUseCase.OrderItemCommand(1L, 10))
        );

        assertThrows(InsufficientStockException.class, () -> createOrderUseCase.execute(command));
    }
}
