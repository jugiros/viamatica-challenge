package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.application.usecase.*;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.OrderNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.*;
import com.viamatica.assessment.orders_management_system.infrastructure.security.CurrentUser;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Order management endpoints")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CreateOrderUseCase createOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetOrdersByUserUseCase getOrdersByUserUseCase;

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order with items")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody CreateOrderRequest request,
            @CurrentUser Long currentUserId) {
        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(
                currentUserId,
                request.items().stream()
                        .map(item -> new CreateOrderUseCase.OrderItemCommand(
                                item.productId(),
                                item.cantidad()
                        ))
                        .toList()
        );
        
        OrderDomain order = createOrderUseCase.execute(command);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve orders with pagination (user's own orders or all if admin)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Page<OrderResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser Long currentUserId,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        List<OrderDomain> orders;
        if (isAdmin) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByUserId(currentUserId);
        }
        
        List<OrderResponse> responses = orders.stream()
                .map(this::toOrderResponse)
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        
        Page<OrderResponse> pageResponse = new PageImpl<>(
                responses.subList(start, end),
                pageable,
                responses.size()
        );
        
        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by ID (owner only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponse> getById(
            @PathVariable Long id,
            @CurrentUser Long currentUserId,
            Authentication authentication) {
        OrderDomain order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        verifyOwnership(order.getUserId(), currentUserId, authentication);
        
        return ResponseEntity.ok(toOrderResponse(order));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm order", description = "Confirm a pending order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponse> confirm(@PathVariable Long id) {
        ConfirmOrderUseCase.Command command = new ConfirmOrderUseCase.Command(id);
        OrderDomain order = confirmOrderUseCase.execute(command);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    @PutMapping("/{id}/pay")
    @Operation(summary = "Pay order", description = "Process payment for an order")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponse> pay(@PathVariable Long id) {
        OrderDomain order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        ProcessPaymentUseCase.Command command = new ProcessPaymentUseCase.Command(
                id,
                com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod.CREDIT_CARD,
                order.getTotal().amount(),
                "PAYMENT-" + System.currentTimeMillis()
        );
        processPaymentUseCase.execute(command);
        
        OrderDomain updatedOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return ResponseEntity.ok(toOrderResponse(updatedOrder));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order with a reason")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        CancelOrderUseCase.Command command = new CancelOrderUseCase.Command(id, "Cancelled by user");
        OrderDomain order = cancelOrderUseCase.execute(command);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get order reports", description = "Retrieve order reports (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> getReports() {
        return ResponseEntity.ok("Reports endpoint - to be implemented");
    }

    private void verifyOwnership(Long orderUserId, Long currentUserId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !orderUserId.equals(currentUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to access this order");
        }
    }

    private OrderResponse toOrderResponse(OrderDomain order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        null,
                        item.getUnitPrice().amount(),
                        item.getQuantity(),
                        item.getSubtotal().amount()
                ))
                .toList();
        
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotal().amount(),
                order.getOrderDate(),
                order.getUpdatedAt(),
                items
        );
    }
}
