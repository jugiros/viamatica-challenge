package com.viamatica.assessment.orders_management_system.infrastructure.persistence.mapper;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderItemDomain;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "unitPrice", qualifiedByName = "bigDecimalToMoney")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    OrderItemDomain toDomain(OrderItemEntity entity);

    @Mapping(target = "unitPrice", qualifiedByName = "moneyToBigDecimal")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItemEntity toEntity(OrderItemDomain domain);

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal price) {
        return price != null ? Money.of(price) : null;
    }

    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.amount() : null;
    }
}
