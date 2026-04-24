package com.viamatica.assessment.orders_management_system.domain.entity;

import com.viamatica.assessment.orders_management_system.domain.exception.InvalidOrderStateTransitionException;
import com.viamatica.assessment.orders_management_system.domain.order.CancelledStatus;
import com.viamatica.assessment.orders_management_system.domain.order.OrderStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PendingStatus;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@Builder
public class OrderDomain {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private Money total;
    private long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDomain> items;

    public static class OrderDomainBuilder {
        private OrderStatus status = new PendingStatus();
        private LocalDateTime createdAt = LocalDateTime.now();
        private List<OrderItemDomain> items = Collections.emptyList();
    }

    public void transitionTo(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStateTransitionException(status.name(), newStatus.name());
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canBeCancelled() {
        return status.canTransitionTo(new CancelledStatus());
    }

    public void cancel() {
        transitionTo(new CancelledStatus());
    }
}
