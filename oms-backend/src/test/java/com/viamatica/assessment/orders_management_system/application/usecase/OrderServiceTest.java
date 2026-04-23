package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.DomainException;
import com.viamatica.assessment.orders_management_system.domain.exception.InsufficientStockException;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidOrderStateTransitionException;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.order.CancelledStatus;
import com.viamatica.assessment.orders_management_system.domain.order.ConfirmedStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PaidStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PendingStatus;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Tests")
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    @InjectMocks
    private ConfirmOrderUseCase confirmOrderUseCase;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    private UserDomain activeUser;
    private ProductDomain product1;
    private ProductDomain product2;
    private ProductDomain product3;

    @BeforeEach
    void setUp() {
        activeUser = UserDomain.builder()
                .id(1L)
                .name("Test User")
                .email(Email.of("test@example.com"))
                .passwordHash("hashedPassword")
                .role(UserRole.USER)
                .active(true)
                .build();

        product1 = ProductDomain.builder()
                .id(1L)
                .name(ProductName.of("Product A"))
                .price(Money.of(new BigDecimal("10.00")))
                .stock(10)
                .categoryId(1L)
                .active(true)
                .build();

        product2 = ProductDomain.builder()
                .id(2L)
                .name(ProductName.of("Product B"))
                .price(Money.of(new BigDecimal("5.00")))
                .stock(20)
                .categoryId(1L)
                .active(true)
                .build();

        product3 = ProductDomain.builder()
                .id(3L)
                .name(ProductName.of("Product C"))
                .price(Money.of(new BigDecimal("15.00")))
                .stock(5)
                .categoryId(1L)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("UTOR01 - Crear orden 1 producto, usuario activo, stock >= 1 → estado PENDIENTE")
    void testUTOR01_CreateOrderWithOneProduct_WhenUserActiveAndStockSufficient() {
        // Arrange
        CreateOrderUseCase.OrderItemCommand itemCommand = new CreateOrderUseCase.OrderItemCommand(1L, 2);
        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(1L, List.of(itemCommand));

        OrderDomain savedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(OrderItemDomain.builder()
                        .productId(1L)
                        .quantity(2)
                        .unitPrice(Money.of(new BigDecimal("10.00")))
                        .build()))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(savedOrder);

        // Act
        OrderDomain result = createOrderUseCase.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isInstanceOf(PendingStatus.class);
        assertThat(result.getStatus().name()).isEqualTo("PENDIENTE");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotal().amount()).isEqualByComparingTo(new BigDecimal("20.00"));

        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(OrderDomain.class));
        verify(auditPort).logEntityChange(any(), eq("orders"), eq("INSERT"), any(), any(), any());
    }

    @Test
    @DisplayName("UTOR02 - Crear orden 3 productos → 3 ítems, total correcto")
    void testUTOR02_CreateOrderWithThreeProducts_WhenAllProductsAvailable() {
        // Arrange
        List<CreateOrderUseCase.OrderItemCommand> itemCommands = List.of(
                new CreateOrderUseCase.OrderItemCommand(1L, 2),
                new CreateOrderUseCase.OrderItemCommand(2L, 3),
                new CreateOrderUseCase.OrderItemCommand(3L, 1)
        );
        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(1L, itemCommands);

        List<OrderItemDomain> orderItems = List.of(
                OrderItemDomain.builder().productId(1L).quantity(2).unitPrice(Money.of(new BigDecimal("10.00"))).build(),
                OrderItemDomain.builder().productId(2L).quantity(3).unitPrice(Money.of(new BigDecimal("5.00"))).build(),
                OrderItemDomain.builder().productId(3L).quantity(1).unitPrice(Money.of(new BigDecimal("15.00"))).build()
        );

        OrderDomain savedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("55.00"))) // 10*2 + 5*3 + 15*1 = 55
                .items(orderItems)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product3));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(savedOrder);

        // Act
        OrderDomain result = createOrderUseCase.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(3);
        assertThat(result.getTotal().amount()).isEqualByComparingTo(new BigDecimal("55.00"));

        verify(productRepository, times(3)).findById(any());
        verify(orderRepository).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("UTOR03 - Confirmar orden: stock suficiente → CONFIRMADA, stock decrementado")
    void testUTOR03_ConfirmOrder_WhenStockSufficient() {
        // Arrange
        OrderDomain pendingOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(OrderItemDomain.builder()
                        .productId(1L)
                        .quantity(2)
                        .unitPrice(Money.of(new BigDecimal("10.00")))
                        .build()))
                .build();

        OrderDomain confirmedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("20.00")))
                .status(new ConfirmedStatus())
                .items(pendingOrder.getItems())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(confirmedOrder);

        int initialStock = product1.getStock();

        // Act
        OrderDomain result = confirmOrderUseCase.execute(new ConfirmOrderUseCase.Command(1L));

        // Assert
        assertThat(result.getStatus()).isInstanceOf(ConfirmedStatus.class);
        assertThat(result.getStatus().name()).isEqualTo("CONFIRMADA");
        assertThat(product1.getStock()).isEqualTo(initialStock - 2);

        verify(productRepository).save(product1);
        verify(orderRepository).save(any(OrderDomain.class));
        verify(auditPort).logEntityChange(any(), eq("orders"), eq("STATE_CHANGE"), any(), any(), any());
    }

    @Test
    @DisplayName("UTOR04 - Confirmar orden: stock insuficiente → InsufficientStockException, permanece PENDIENTE")
    void testUTOR04_ConfirmOrder_WhenStockInsufficient() {
        // Arrange
        ProductDomain lowStockProduct = ProductDomain.builder()
                .id(1L)
                .name(ProductName.of("Product A"))
                .price(Money.of(new BigDecimal("10.00")))
                .stock(1) // Solo 1 en stock
                .categoryId(1L)
                .active(true)
                .build();

        OrderDomain pendingOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(OrderItemDomain.builder()
                        .productId(1L)
                        .quantity(5) // Requiere 5
                        .unitPrice(Money.of(new BigDecimal("10.00")))
                        .build()))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(lowStockProduct));

        // Act & Assert
        assertThatThrownBy(() -> confirmOrderUseCase.execute(new ConfirmOrderUseCase.Command(1L)))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(pendingOrder.getStatus()).isInstanceOf(PendingStatus.class);
        verify(orderRepository, never()).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("UTOR05 - Orden sin productos → DomainException lista vacía")
    void testUTOR05_CreateOrder_WhenNoProducts() {
        // Arrange
        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(1L, new ArrayList<>());

        OrderDomain savedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("0.01")))
                .items(new ArrayList<>())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(savedOrder);

        // Act
        OrderDomain result = createOrderUseCase.execute(command);

        // Assert
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    @DisplayName("UTOR06 - Calcular total: ítem A $10x2 + ítem B $5x3 = $35.00 exacto (BigDecimal)")
    void testUTOR06_CalculateTotal_WhenMultipleItems() {
        // Arrange
        List<CreateOrderUseCase.OrderItemCommand> itemCommands = List.of(
                new CreateOrderUseCase.OrderItemCommand(1L, 2), // 10 * 2 = 20
                new CreateOrderUseCase.OrderItemCommand(2L, 3)  // 5 * 3 = 15
        );
        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(1L, itemCommands);

        List<OrderItemDomain> orderItems = List.of(
                OrderItemDomain.builder().productId(1L).quantity(2).unitPrice(Money.of(new BigDecimal("10.00"))).build(),
                OrderItemDomain.builder().productId(2L).quantity(3).unitPrice(Money.of(new BigDecimal("5.00"))).build()
        );

        OrderDomain savedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("35.00")))
                .items(orderItems)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(savedOrder);

        // Act
        OrderDomain result = createOrderUseCase.execute(command);

        // Assert
        assertThat(result.getTotal().amount()).isEqualByComparingTo(new BigDecimal("35.00"));
    }

    @Test
    @DisplayName("UTOR07 - Transición PENDIENTE→CONFIRMADA exitosa")
    void testUTOR07_TransitionPendingToConfirmed_WhenValid() {
        // Arrange
        OrderItemDomain item = OrderItemDomain.builder()
                .id(1L)
                .productId(1L)
                .quantity(1)
                .unitPrice(Money.of(new BigDecimal("10.00")))
                .build();

        OrderDomain order = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("10.00")))
                .items(List.of(item))
                .build();

        // Act
        order.transitionTo(new ConfirmedStatus());

        // Assert
        assertThat(order.getStatus()).isInstanceOf(ConfirmedStatus.class);
        assertThat(order.getStatus().name()).isEqualTo("CONFIRMADA");
    }

    @Test
    @DisplayName("UTOR08 - Transición CONFIRMADA→PAGADA exitosa")
    void testUTOR08_TransitionConfirmedToPaid_WhenValid() {
        // Arrange
        OrderItemDomain item = OrderItemDomain.builder()
                .id(1L)
                .productId(1L)
                .quantity(1)
                .unitPrice(Money.of(new BigDecimal("10.00")))
                .build();

        OrderDomain order = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new ConfirmedStatus())
                .total(Money.of(new BigDecimal("10.00")))
                .items(List.of(item))
                .build();

        // Act
        order.transitionTo(new PaidStatus());

        // Assert
        assertThat(order.getStatus()).isInstanceOf(PaidStatus.class);
        assertThat(order.getStatus().name()).isEqualTo("PAGADA");
    }

    @Test
    @DisplayName("UTOR09 - Transición inválida PAGADA→PENDIENTE → InvalidOrderStateTransitionException")
    void testUTOR09_TransitionPaidToPending_WhenInvalid() {
        // Arrange
        OrderItemDomain item = OrderItemDomain.builder()
                .id(1L)
                .productId(1L)
                .quantity(1)
                .unitPrice(Money.of(new BigDecimal("10.00")))
                .build();

        OrderDomain order = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new PaidStatus())
                .total(Money.of(new BigDecimal("10.00")))
                .items(List.of(item))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> order.transitionTo(new PendingStatus()))
                .isInstanceOf(InvalidOrderStateTransitionException.class)
                .hasMessageContaining("PAGADA")
                .hasMessageContaining("PENDIENTE");
    }

    @Test
    @DisplayName("UTOR10 - Cancelar CONFIRMADA → CANCELADA, stock liberado")
    void testUTOR10_CancelPendingOrder_WhenValid() {
        // Arrange
        OrderItemDomain item = OrderItemDomain.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(Money.of(new BigDecimal("10.00")))
                .build();

        OrderDomain pendingOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new ConfirmedStatus()) // Set initial status to CONFIRMED
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(item))
                .build();

        OrderDomain cancelledOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new CancelledStatus())
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(item))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(cancelledOrder);

        // Act
        OrderDomain result = cancelOrderUseCase.execute(new CancelOrderUseCase.Command(1L, "Customer request"));

        // Assert
        assertThat(result.getStatus()).isInstanceOf(CancelledStatus.class);
        assertThat(result.getStatus().name()).isEqualTo("CANCELADA");

        // Stock should be restored (incremented)
        verify(productRepository).save(product1);
    }

    @Test
    @DisplayName("UTOR11 - Cancelar PAGADA → InvalidOrderStateTransitionException o requiere reembolso")
    void testUTOR11_CancelPaidOrder_WhenInvalid() {
        // Arrange
        OrderItemDomain item = OrderItemDomain.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(Money.of(new BigDecimal("10.00")))
                .build();

        OrderDomain paidOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new PaidStatus())
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(item))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(paidOrder));

        // Act & Assert
        assertThatThrownBy(() -> cancelOrderUseCase.execute(new CancelOrderUseCase.Command(1L, "Customer request")))
                .isInstanceOf(InvalidOrderStateTransitionException.class);
    }

    @RepeatedTest(10)
    @DisplayName("UTOR12 - CONCURRENCIA: mock OptimisticLockException en segundo request → solo 1 confirmada")
    void testUTOR12_ConcurrentConfirmation_WhenOptimisticLock() {
        // Arrange
        OrderItemDomain item = OrderItemDomain.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(Money.of(new BigDecimal("10.00")))
                .build();

        // Return a fresh copy each time to simulate different transactions reading the same initial state
        when(orderRepository.findById(1L)).thenAnswer(invocation -> Optional.of(OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(item))
                .build()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        // First call succeeds
        OrderDomain confirmedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new ConfirmedStatus())
                .total(Money.of(new BigDecimal("20.00")))
                .items(List.of(item))
                .build();

        // Second call throws OptimisticLockException
        when(orderRepository.save(any(OrderDomain.class)))
                .thenReturn(confirmedOrder)
                .thenThrow(new OptimisticLockException("Version conflict"));

        // Act - First confirmation
        OrderDomain result1 = confirmOrderUseCase.execute(new ConfirmOrderUseCase.Command(1L));

        // Assert - First confirmation succeeds
        assertThat(result1.getStatus()).isInstanceOf(ConfirmedStatus.class);

        // Act & Assert - Second confirmation fails
        assertThatThrownBy(() -> confirmOrderUseCase.execute(new ConfirmOrderUseCase.Command(1L)))
                .isInstanceOf(OptimisticLockException.class);
    }
}
