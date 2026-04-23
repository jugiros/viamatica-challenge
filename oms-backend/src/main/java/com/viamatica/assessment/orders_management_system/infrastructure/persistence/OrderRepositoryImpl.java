package com.viamatica.assessment.orders_management_system.infrastructure.persistence;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.order.*;
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
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDomain> findByOrderNumber(String orderNumber) {
        return jpaRepository.findByOrderNumber(orderNumber).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDomain> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDomain> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    private OrderDomain toDomain(OrderEntity entity) {
        List<OrderItemDomain> items = entity.getItems().stream()
                .map(this::toOrderItemDomain)
                .collect(Collectors.toList());

        return OrderDomain.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .userId(entity.getUserId())
                .total(Money.of(entity.getTotal()))
                .status(convertStatusEntityToStatus(entity.getStatus()))
                .items(items)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private OrderEntity toEntity(OrderDomain domain) {
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.getId());
        entity.setOrderNumber(domain.getOrderNumber());
        entity.setUserId(domain.getUserId());
        entity.setTotal(domain.getTotal().amount());
        entity.setStatus(convertStatusToStatusEntity(domain.getStatus()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        List<OrderItemEntity> itemEntities = domain.getItems().stream()
                .map(this::toOrderItemEntity)
                .collect(Collectors.toList());
        entity.setItems(itemEntities);

        return entity;
    }

    private OrderItemDomain toOrderItemDomain(OrderItemEntity entity) {
        return OrderItemDomain.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .unitPrice(Money.of(entity.getUnitPrice()))
                .build();
    }

    private OrderItemEntity toOrderItemEntity(OrderItemDomain domain) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice().amount());
        entity.setSubtotal(domain.getSubtotal().amount());
        return entity;
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
        if (status == null) {
            return null;
        }
        return switch (status.name()) {
            case "PENDING" -> OrderStatusEntity.PENDIENTE;
            case "CONFIRMED" -> OrderStatusEntity.CONFIRMADA;
            case "PAID" -> OrderStatusEntity.PAGADA;
            case "SHIPPED" -> OrderStatusEntity.ENVIADA;
            case "CANCELLED" -> OrderStatusEntity.CANCELADA;
            default -> throw new IllegalArgumentException("Unknown status: " + status.name());
        };
    }
}
