package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import com.viamatica.assessment.orders_management_system.application.usecase.CancelOrderUseCase;
import com.viamatica.assessment.orders_management_system.application.usecase.ConfirmOrderUseCase;
import com.viamatica.assessment.orders_management_system.application.usecase.CreateOrderUseCase;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.OrderNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.CancelOrderRequest;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.CreateOrderRequest;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.OrderItemResponse;
import com.viamatica.assessment.orders_management_system.infrastructure.controller.dto.OrderResponse;
import com.viamatica.assessment.orders_management_system.infrastructure.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderRepository orderRepository;

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order with a list of products and quantities")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<OrderResponse> create(
            @RequestBody CreateOrderRequest request,
            @CurrentUser Long userId) {
        List<CreateOrderUseCase.OrderItemCommand> itemCommands = request.items().stream()
                .map(item -> new CreateOrderUseCase.OrderItemCommand(item.productId(), item.quantity()))
                .collect(Collectors.toList());

        CreateOrderUseCase.Command command = new CreateOrderUseCase.Command(userId, itemCommands);
        OrderDomain order = createOrderUseCase.execute(command);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    @GetMapping
    @Operation(summary = "Get all orders for current user", description = "Retrieve a list of orders for the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<OrderResponse>> getAll(@CurrentUser Long userId) {
        List<OrderDomain> orders = orderRepository.findByUserId(userId);
        return ResponseEntity.ok(orders.stream().map(this::toOrderResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not your order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> confirm(@PathVariable Long id) {
        ConfirmOrderUseCase.Command command = new ConfirmOrderUseCase.Command(id);
        OrderDomain order = confirmOrderUseCase.execute(command);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order with a reason")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid state transition"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id, @RequestBody CancelOrderRequest request) {
        CancelOrderUseCase.Command command = new CancelOrderUseCase.Command(id, request.reason());
        OrderDomain order = cancelOrderUseCase.execute(command);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    @GetMapping("/reports")
    @Operation(summary = "Get order reports", description = "Retrieve order reports (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin only")
    })
    public ResponseEntity<String> getReports() {
        return ResponseEntity.ok("Reports endpoint - to be implemented");
    }

    private void verifyOwnership(Long orderUserId, Long currentUserId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !orderUserId.equals(currentUserId)) {
            throw new AccessDeniedException("You are not authorized to access this order");
        }
    }

    private OrderResponse toOrderResponse(OrderDomain order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotal().amount(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                itemResponses
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderItemDomain item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice().amount(),
                item.getSubtotal().amount()
        );
    }
}
