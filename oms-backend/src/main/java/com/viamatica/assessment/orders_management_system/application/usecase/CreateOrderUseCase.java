package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case for creating a new order.
 * Validates user active, products exist with stock, calculates total automatically.
 * Initial state is PENDIENTE.
 */
@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AuditPort auditPort;

    public record OrderItemCommand(
            Long productId,
            int quantity
    ) {}

    public record Command(
            Long userId,
            List<OrderItemCommand> items
    ) {}

    public OrderDomain execute(Command command) {
        UserDomain user = userRepository.findById(command.userId)
                .orElseThrow(() -> new UserNotFoundException(command.userId));

        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is inactive");
        }

        List<OrderItemDomain> orderItems = new ArrayList<>();
        Money total = Money.of(BigDecimal.ZERO);

        for (OrderItemCommand itemCommand : command.items) {
            ProductDomain product = productRepository.findById(itemCommand.productId)
                    .orElseThrow(() -> new ProductNotFoundException(itemCommand.productId));

            if (!product.isActive()) {
                throw new IllegalArgumentException("Product is inactive: " + product.getName());
            }

            if (!product.hasSufficientStock(itemCommand.quantity)) {
                throw new InsufficientStockException(
                        itemCommand.productId,
                        itemCommand.quantity,
                        product.getStock()
                );
            }

            OrderItemDomain orderItem = OrderItemDomain.builder()
                    .productId(itemCommand.productId)
                    .quantity(itemCommand.quantity)
                    .unitPrice(product.getPrice())
                    .build();

            orderItems.add(orderItem);
            total = total.add(orderItem.getSubtotal());
        }

        OrderDomain order = OrderDomain.builder()
                .userId(command.userId)
                .total(total)
                .items(orderItems)
                .build();

        OrderDomain savedOrder = orderRepository.save(order);

        auditPort.logEntityChange(
                savedOrder.getId(),
                "orders",
                "INSERT",
                savedOrder.getId(),
                null,
                "{\"id\":" + savedOrder.getId() + ",\"orderNumber\":\"" + savedOrder.getOrderNumber() + "\"}"
        );

        return savedOrder;
    }
}
