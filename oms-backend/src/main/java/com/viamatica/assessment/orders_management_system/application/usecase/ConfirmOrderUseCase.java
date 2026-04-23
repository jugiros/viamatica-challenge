package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.InsufficientStockException;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidOrderStateTransitionException;
import com.viamatica.assessment.orders_management_system.domain.exception.OrderNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.exception.ProductNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.order.ConfirmedStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PendingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmOrderUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AuditPort auditPort;

    public record Command(Long orderId) {}

    public OrderDomain execute(Command command) {
        OrderDomain order = orderRepository.findById(command.orderId)
                .orElseThrow(() -> new OrderNotFoundException(command.orderId));

        String previousStatus = order.getStatus().name();

        if (!(order.getStatus() instanceof PendingStatus)) {
            throw new InvalidOrderStateTransitionException(previousStatus, "CONFIRMADA");
        }

        for (OrderItemDomain item : order.getItems()) {
            ProductDomain product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));

            if (!product.hasSufficientStock(item.getQuantity())) {
                throw new InsufficientStockException(
                        item.getProductId(),
                        item.getQuantity(),
                        product.getStock()
                );
            }

            product.decrementStock(item.getQuantity());
            productRepository.save(product);
        }

        order.transitionTo(new ConfirmedStatus());
        OrderDomain savedOrder = orderRepository.save(order);

        auditPort.logEntityChange(
                savedOrder.getId(),
                "orders",
                "STATE_CHANGE",
                savedOrder.getId(),
                "{\"status\":\"" + previousStatus + "\"}",
                "{\"status\":\"" + savedOrder.getStatus().name() + "\"}"
        );

        return savedOrder;
    }
}
