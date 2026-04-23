package com.viamatica.assessment.orders_management_system.infrastructure.persistence;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.OrderNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.order.CancelledStatus;
import com.viamatica.assessment.orders_management_system.domain.order.ConfirmedStatus;
import com.viamatica.assessment.orders_management_system.domain.order.OrderStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PaidStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PendingStatus;
import com.viamatica.assessment.orders_management_system.domain.order.ShippedStatus;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderItemEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderStatusEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    @Transactional
    public OrderDomain save(OrderDomain order) {
        OrderEntity entity = toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDomain> findById(Long id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDomain> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumberAndDeletedAtIsNull(orderNumber)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDomain> findByUserId(Long userId) {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDomain> findAll() {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getDeletedAt() == null)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        OrderEntity entity = jpaRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setDeletedAt(java.time.LocalDateTime.now());
            jpaRepository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).isPresent();
    }

    private OrderDomain toDomain(OrderEntity entity) {
        List<OrderItemDomain> items = entity.getItems().stream()
                .map(this::toOrderItemDomain)
                .collect(Collectors.toList());

        OrderDomain domain = OrderDomain.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .userId(entity.getUserId())
                .total(Money.of(entity.getTotal()))
                .items(items)
                .orderDate(entity.getOrderDate())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .version(entity.getVersion())
                .build();

        // Set status using the transitionTo method
        OrderStatus status = convertStatusEntityToStatus(entity.getStatus());
        domain.transitionTo(status);

        return domain;
    }

    private OrderStatus convertStatusEntityToStatus(OrderStatusEntity statusEntity) {
        return switch (statusEntity) {
            case PENDIENTE -> new PendingStatus();
            case CONFIRMADA -> new ConfirmedStatus();
            case PAGADA -> new PaidStatus();
            case ENVIADA -> new ShippedStatus();
            case CANCELADA -> new CancelledStatus();
        };
    }

    private OrderStatusEntity convertStatusToStatusEntity(OrderStatus status) {
        return switch (status.name()) {
            case "PENDIENTE" -> OrderStatusEntity.PENDIENTE;
            case "CONFIRMADA" -> OrderStatusEntity.CONFIRMADA;
            case "PAGADA" -> OrderStatusEntity.PAGADA;
            case "ENVIADA" -> OrderStatusEntity.ENVIADA;
            case "CANCELADA" -> OrderStatusEntity.CANCELADA;
            default -> throw new IllegalArgumentException("Unknown status: " + status.name());
        };
    }

    private OrderItemDomain toOrderItemDomain(OrderItemEntity entity) {
        return OrderItemDomain.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .unitPrice(Money.of(entity.getUnitPrice()))
                .build();
    }

    private OrderEntity toEntity(OrderDomain domain) {
        OrderEntity entity = new OrderEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setOrderNumber(domain.getOrderNumber());
        entity.setUserId(domain.getUserId());
        entity.setTotal(domain.getTotal().amount());
        entity.setStatus(convertStatusToStatusEntity(domain.getStatus()));
        entity.setOrderDate(domain.getOrderDate());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
        entity.setVersion(domain.getVersion());

        List<OrderItemEntity> itemEntities = domain.getItems().stream()
                .map(this::toOrderItemEntity)
                .collect(Collectors.toList());
        entity.setItems(itemEntities);

        return entity;
    }

    private String convertStatusToString(OrderStatus status) {
        if (status instanceof PendingStatus) return "PENDIENTE";
        if (status instanceof ConfirmedStatus) return "CONFIRMADA";
        if (status instanceof PaidStatus) return "PAGADA";
        if (status instanceof ShippedStatus) return "ENVIADA";
        if (status instanceof CancelledStatus) return "CANCELADA";
        throw new IllegalArgumentException("Unknown status: " + status.getClass().getSimpleName());
    }

    private OrderItemEntity toOrderItemEntity(OrderItemDomain domain) {
        OrderItemEntity entity = new OrderItemEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setProductId(domain.getProductId());
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice().amount());
        entity.setSubtotal(domain.getSubtotal().amount());
        return entity;
    }
}
