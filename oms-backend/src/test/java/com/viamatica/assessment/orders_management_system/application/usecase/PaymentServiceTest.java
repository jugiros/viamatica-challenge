package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.DomainException;
import com.viamatica.assessment.orders_management_system.domain.exception.DuplicatePaymentException;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidOrderStateTransitionException;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentStatus;
import com.viamatica.assessment.orders_management_system.domain.order.ConfirmedStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PaidStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PendingStatus;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.NotificationPort;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.PaymentRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Tests")
class PaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AuditPort auditPort;

    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private ProcessPaymentUseCase processPaymentUseCase;

    private OrderDomain confirmedOrder;
    private OrderDomain pendingOrder;
    private OrderDomain paidOrder;

    @BeforeEach
    void setUp() {
        OrderItemDomain item1 = OrderItemDomain.builder()
                .id(1L)
                .productId(1L)
                .quantity(2)
                .unitPrice(Money.of(new BigDecimal("50.00")))
                .build();

        confirmedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new ConfirmedStatus())
                .total(Money.of(new BigDecimal("100.00")))
                .items(List.of(item1))
                .build();

        OrderItemDomain item2 = OrderItemDomain.builder()
                .id(2L)
                .productId(1L)
                .quantity(1)
                .unitPrice(Money.of(new BigDecimal("50.00")))
                .build();

        pendingOrder = OrderDomain.builder()
                .id(2L)
                .userId(1L)
                .status(new PendingStatus())
                .total(Money.of(new BigDecimal("50.00")))
                .items(List.of(item2))
                .build();

        OrderItemDomain item3 = OrderItemDomain.builder()
                .id(3L)
                .productId(1L)
                .quantity(1)
                .unitPrice(Money.of(new BigDecimal("75.00")))
                .build();

        paidOrder = OrderDomain.builder()
                .id(3L)
                .userId(1L)
                .status(new PaidStatus())
                .total(Money.of(new BigDecimal("75.00")))
                .items(List.of(item3))
                .build();
    }

    @Test
    @DisplayName("UTPA01 - Pago exitoso orden CONFIRMADA, monto correcto → PAGADA")
    void testUTPA01_ProcessPayment_WhenOrderConfirmedAndAmountCorrect() {
        // Arrange
        ProcessPaymentUseCase.Command command = new ProcessPaymentUseCase.Command(
                1L,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("100.00"),
                "EXT-REF-12345"
        );

        PaymentDomain savedPayment = PaymentDomain.builder()
                .id(1L)
                .orderId(1L)
                .method(PaymentMethod.CREDIT_CARD)
                .amount(Money.of(new BigDecimal("100.00")))
                .status(PaymentStatus.COMPLETED)
                .externalReference("EXT-REF-12345")
                .build();

        OrderDomain updatedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new PaidStatus())
                .total(Money.of(new BigDecimal("100.00")))
                .items(confirmedOrder.getItems())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(confirmedOrder));
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
        when(paymentRepository.save(any(PaymentDomain.class))).thenReturn(savedPayment);
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(updatedOrder);

        // Act
        PaymentDomain result = processPaymentUseCase.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getAmount().amount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.getMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.COMPLETED);

        verify(orderRepository).findById(1L);
        verify(paymentRepository).existsByOrderId(1L);
        verify(paymentRepository).save(any(PaymentDomain.class));
        verify(orderRepository).save(any(OrderDomain.class));
        verify(auditPort).logEntityChange(any(), eq("payments"), eq("INSERT"), any(), any(), any());
        verify(notificationPort).sendPaymentNotification(eq(1L), eq(1L));
    }

    @Test
    @DisplayName("UTPA02 - Pagar orden PENDIENTE → DomainException no confirmada")
    void testUTPA02_ProcessPayment_WhenOrderNotConfirmed() {
        // Arrange
        ProcessPaymentUseCase.Command command = new ProcessPaymentUseCase.Command(
                2L,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("50.00"),
                "EXT-REF-12345"
        );

        when(orderRepository.findById(2L)).thenReturn(Optional.of(pendingOrder));

        // Act & Assert
        assertThatThrownBy(() -> processPaymentUseCase.execute(command))
                .isInstanceOf(InvalidOrderStateTransitionException.class)
                .hasMessageContaining("PENDIENTE")
                .hasMessageContaining("PAGADA");

        verify(orderRepository).findById(2L);
        verify(paymentRepository, never()).save(any(PaymentDomain.class));
        verify(orderRepository, never()).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("UTPA03 - Pago duplicado orden ya PAGADA → DuplicatePaymentException")
    void testUTPA03_ProcessPayment_WhenPaymentAlreadyExists() {
        // Arrange
        ProcessPaymentUseCase.Command command = new ProcessPaymentUseCase.Command(
                1L,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("100.00"),
                "EXT-REF-12345"
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(confirmedOrder));
        when(paymentRepository.existsByOrderId(1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> processPaymentUseCase.execute(command))
                .isInstanceOf(DuplicatePaymentException.class);

        verify(orderRepository).findById(1L);
        verify(paymentRepository).existsByOrderId(1L);
        verify(paymentRepository, never()).save(any(PaymentDomain.class));
        verify(orderRepository, never()).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("UTPA04 - Monto incorrecto → DomainException discrepancia de monto")
    void testUTPA04_ProcessPayment_WhenAmountMismatch() {
        // Arrange
        ProcessPaymentUseCase.Command command = new ProcessPaymentUseCase.Command(
                1L,
                PaymentMethod.CREDIT_CARD,
                new BigDecimal("50.00"), // Monto incorrecto, debería ser 100.00
                "EXT-REF-12345"
        );

        when(orderRepository.findById(1L)).thenReturn(Optional.of(confirmedOrder));
        when(paymentRepository.existsByOrderId(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> processPaymentUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Payment amount does not match order total");

        verify(orderRepository).findById(1L);
        verify(paymentRepository).existsByOrderId(1L);
        verify(paymentRepository, never()).save(any(PaymentDomain.class));
        verify(orderRepository, never()).save(any(OrderDomain.class));
    }
}
