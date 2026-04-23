package com.viamatica.assessment.orders_management_system.infrastructure.persistence.mapper;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.order.OrderStatus;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderItemEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

/**
 * MapStruct mapper for converting between OrderEntity and OrderDomain.
 */
@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "total", qualifiedByName = "bigDecimalToTotalMoney")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "items", ignore = true)
    OrderDomain toDomain(OrderEntity entity);

    @Mapping(target = "total", qualifiedByName = "totalMoneyToBigDecimal")
    @Mapping(target = "status", qualifiedByName = "statusToStatusEntity")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDomain(OrderDomain domain, @MappingTarget OrderEntity entity);

    @Named("bigDecimalToTotalMoney")
    default Money bigDecimalToTotalMoney(BigDecimal total) {
        return total != null ? Money.of(total) : null;
    }

    @Named("totalMoneyToBigDecimal")
    default BigDecimal totalMoneyToBigDecimal(Money money) {
        return money != null ? money.amount() : null;
    }

    @Named("statusToStatusEntity")
    default OrderStatusEntity statusToStatusEntity(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return switch (status.name()) {
            case "PENDIENTE" -> OrderStatusEntity.PENDIENTE;
            case "CONFIRMADA" -> OrderStatusEntity.CONFIRMADA;
            case "PAGADA" -> OrderStatusEntity.PAGADA;
            case "ENVIADA" -> OrderStatusEntity.ENVIADA;
            case "CANCELADA" -> OrderStatusEntity.CANCELADA;
            default -> throw new IllegalArgumentException("Unknown status: " + status.name());
        };
    }

    default OrderStatus statusEntityToStatus(OrderStatusEntity statusEntity) {
        if (statusEntity == null) {
            return null;
        }
        return switch (statusEntity) {
            case PENDIENTE -> new com.viamatica.assessment.orders_management_system.domain.order.PendingStatus();
            case CONFIRMADA -> new com.viamatica.assessment.orders_management_system.domain.order.ConfirmedStatus();
            case PAGADA -> new com.viamatica.assessment.orders_management_system.domain.order.PaidStatus();
            case ENVIADA -> new com.viamatica.assessment.orders_management_system.domain.order.ShippedStatus();
            case CANCELADA -> new com.viamatica.assessment.orders_management_system.domain.order.CancelledStatus();
        };
    }

    List<OrderItemDomain> orderItemEntitiesToDomains(List<OrderItemEntity> entities);

    List<OrderItemEntity> orderItemDomainsToEntities(List<OrderItemDomain> domains);
}
