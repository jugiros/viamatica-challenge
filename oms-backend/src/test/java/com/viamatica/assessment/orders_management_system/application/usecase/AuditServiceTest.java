package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.order.ConfirmedStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PendingStatus;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
@DisplayName("Audit Service Tests")
class AuditServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuditPort auditPort;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @InjectMocks
    private ConfirmOrderUseCase confirmOrderUseCase;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Captor
    private ArgumentCaptor<String> tableCaptor;

    @Captor
    private ArgumentCaptor<String> operationCaptor;

    @Captor
    private ArgumentCaptor<Long> entityIdCaptor;

    @Captor
    private ArgumentCaptor<String> previousValuesCaptor;

    @Captor
    private ArgumentCaptor<String> newValuesCaptor;

    @BeforeEach
    void setUp() {
        // Setup common test data if needed
    }

    @Test
    @DisplayName("UTAU01 - Crear usuario genera log INSERT en AUDIT_LOG")
    void testUTAU01_CreateUser_GeneratesInsertAuditLog() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Test User",
                "test@example.com",
                "Password123",
                UserRole.USER
        );

        UserDomain savedUser = UserDomain.builder()
                .id(1L)
                .name("Test User")
                .email(Email.of("test@example.com"))
                .passwordHash("hashedPassword")
                .role(UserRole.USER)
                .active(true)
                .build();

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.save(any(UserDomain.class))).thenReturn(savedUser);

        // Act
        UserDomain result = registerUserUseCase.execute(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(auditPort).logEntityChange(
                userIdCaptor.capture(),
                tableCaptor.capture(),
                operationCaptor.capture(),
                entityIdCaptor.capture(),
                previousValuesCaptor.capture(),
                newValuesCaptor.capture()
        );

        // Verify audit log parameters
        assertThat(userIdCaptor.getValue()).isEqualTo(1L);
        assertThat(tableCaptor.getValue()).isEqualTo("users");
        assertThat(operationCaptor.getValue()).isEqualTo("INSERT");
        assertThat(entityIdCaptor.getValue()).isEqualTo(1L);
        assertThat(previousValuesCaptor.getValue()).isNull();
        assertThat(newValuesCaptor.getValue())
                .contains("\"id\":1")
                .contains("\"email\":\"test@example.com\"");
    }

    @Test
    @DisplayName("UTAU02 - Cambio de estado genera log con estado anterior y nuevo")
    void testUTAU02_StateChange_GeneratesAuditLogWithPreviousAndNewState() {
        // Arrange
        OrderDomain pendingOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new PendingStatus())
                .total(Money.of(new BigDecimal("100.00")))
                .items(List.of(OrderItemDomain.builder()
                        .productId(1L)
                        .quantity(2)
                        .unitPrice(Money.of(new BigDecimal("50.00")))
                        .build()))
                .build();

        OrderDomain confirmedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new ConfirmedStatus())
                .total(Money.of(new BigDecimal("100.00")))
                .items(pendingOrder.getItems())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(confirmedOrder);

        // Mock product to avoid InsufficientStockException
        when(productRepository.findById(1L)).thenReturn(Optional.of(
                com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain.builder()
                        .id(1L)
                        .name(com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName.of("Test Product"))
                        .price(Money.of(new BigDecimal("50.00")))
                        .stock(10)
                        .categoryId(1L)
                        .active(true)
                        .build()
        ));

        // Act
        OrderDomain result = confirmOrderUseCase.execute(new ConfirmOrderUseCase.Command(1L));

        // Assert
        verify(auditPort).logEntityChange(
                userIdCaptor.capture(),
                tableCaptor.capture(),
                operationCaptor.capture(),
                entityIdCaptor.capture(),
                previousValuesCaptor.capture(),
                newValuesCaptor.capture()
        );

        // Verify audit log parameters
        assertThat(tableCaptor.getValue()).isEqualTo("orders");
        assertThat(operationCaptor.getValue()).isEqualTo("STATE_CHANGE");
        assertThat(entityIdCaptor.getValue()).isEqualTo(1L);
        assertThat(previousValuesCaptor.getValue())
                .contains("\"status\":\"PENDIENTE\"");
        assertThat(newValuesCaptor.getValue())
                .contains("\"status\":\"CONFIRMADA\"");
    }

    @Test
    @DisplayName("UTAU03 - Log UPDATE contiene datos_anteriores y datos_nuevos JSON")
    void testUTAU03_UpdateLog_ContainsPreviousAndNewDataAsJSON() {
        // Arrange
        OrderDomain pendingOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new PendingStatus())
                .total(Money.of(new BigDecimal("100.00")))
                .items(List.of(OrderItemDomain.builder()
                        .productId(1L)
                        .quantity(2)
                        .unitPrice(Money.of(new BigDecimal("50.00")))
                        .build()))
                .build();

        OrderDomain confirmedOrder = OrderDomain.builder()
                .id(1L)
                .userId(1L)
                .status(new ConfirmedStatus())
                .total(Money.of(new BigDecimal("100.00")))
                .items(pendingOrder.getItems())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(
                com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain.builder()
                        .id(1L)
                        .name(com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName.of("Test Product"))
                        .price(Money.of(new BigDecimal("50.00")))
                        .stock(10)
                        .categoryId(1L)
                        .active(true)
                        .build()
        ));
        when(orderRepository.save(any(OrderDomain.class))).thenReturn(confirmedOrder);

        // Act
        confirmOrderUseCase.execute(new ConfirmOrderUseCase.Command(1L));

        // Assert
        verify(auditPort).logEntityChange(
                any(),
                any(),
                any(),
                any(),
                previousValuesCaptor.capture(),
                newValuesCaptor.capture()
        );

        // Verify JSON structure
        String previousValues = previousValuesCaptor.getValue();
        String newValues = newValuesCaptor.getValue();

        assertThat(previousValues)
                .startsWith("{")
                .endsWith("}")
                .contains("status");

        assertThat(newValues)
                .startsWith("{")
                .endsWith("}")
                .contains("status");
    }

    @Test
    @DisplayName("UTAU04 - Log identifica userId del JWT registrado")
    void testUTAU04_AuditLog_IdentifiesUserIdFromJWT() {
        // Arrange
        RegisterUserUseCase.Command command = new RegisterUserUseCase.Command(
                "Admin User",
                "admin@example.com",
                "AdminPass123",
                UserRole.ADMIN
        );

        UserDomain savedUser = UserDomain.builder()
                .id(100L)
                .name("Admin User")
                .email(Email.of("admin@example.com"))
                .passwordHash("hashedPassword")
                .role(UserRole.ADMIN)
                .active(true)
                .build();

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.save(any(UserDomain.class))).thenReturn(savedUser);

        // Act
        registerUserUseCase.execute(command);

        // Assert
        verify(auditPort).logEntityChange(
                userIdCaptor.capture(),
                any(),
                any(),
                any(),
                any(),
                any()
        );

        // Verify that userId is captured in audit log
        assertThat(userIdCaptor.getValue()).isEqualTo(100L);
    }
}
