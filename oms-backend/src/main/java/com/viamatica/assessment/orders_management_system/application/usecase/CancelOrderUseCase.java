package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidOrderStateTransitionException;
import com.viamatica.assessment.orders_management_system.domain.exception.OrderNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.exception.ProductNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use case for cancelling an order.
 * Validates valid transition with sealed OrderStatus.
 * Releases stock if order was CONFIRMADA.
 */
@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuditPort auditPort;

    public record Command(Long orderId, String reason) {}

    public OrderDomain execute(Command command) {
        OrderDomain order = orderRepository.findById(command.orderId)
                .orElseThrow(() -> new OrderNotFoundException(command.orderId));

        String previousStatus = order.getStatus().name();

        if (!order.canBeCancelled()) {
            throw new InvalidOrderStateTransitionException(previousStatus, "CANCELADA");
        }

        boolean wasConfirmed = previousStatus.equals("CONFIRMADA");

        if (wasConfirmed) {
            for (OrderItemDomain item : order.getItems()) {
                ProductDomain product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));

                product.incrementStock(item.getQuantity());
                productRepository.save(product);
            }
        }

        order.cancel();
        OrderDomain savedOrder = orderRepository.save(order);

        auditPort.logEntityChange(
                savedOrder.getId(),
                "orders",
                "STATE_CHANGE",
                savedOrder.getId(),
                "{\"status\":\"" + previousStatus + "\"}",
                "{\"status\":\"" + savedOrder.getStatus().name() + "\",\"reason\":\"" + command.reason + "\"}"
        );

        return savedOrder;
    }
}
