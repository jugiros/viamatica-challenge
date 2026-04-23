package com.viamatica.assessment.orders_management_system.infrastructure.persistence.mapper;

import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentStatus;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "amount", qualifiedByName = "bigDecimalToMoney")
    @Mapping(target = "createdAt", ignore = true)
    PaymentDomain toDomain(PaymentEntity entity);

    @Mapping(target = "amount", qualifiedByName = "moneyToBigDecimal")
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    PaymentEntity toEntity(PaymentDomain domain);

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal amount) {
        return amount != null ? Money.of(amount) : null;
    }

    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.amount() : null;
    }
}
