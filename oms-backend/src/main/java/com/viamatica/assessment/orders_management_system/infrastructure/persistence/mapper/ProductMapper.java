package com.viamatica.assessment.orders_management_system.infrastructure.persistence.mapper;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

/**
 * MapStruct mapper for converting between ProductEntity and ProductDomain.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "name", qualifiedByName = "stringToProductName")
    @Mapping(target = "price", qualifiedByName = "bigDecimalToMoney")
    ProductDomain toDomain(ProductEntity entity);

    @Mapping(target = "name", qualifiedByName = "productNameToString")
    @Mapping(target = "price", qualifiedByName = "moneyToBigDecimal")
    ProductEntity toEntity(ProductDomain domain);

    @Named("stringToProductName")
    default ProductName stringToProductName(String name) {
        return name != null ? new ProductName(name) : null;
    }

    @Named("productNameToString")
    default String productNameToString(ProductName name) {
        return name != null ? name.value() : null;
    }

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal price) {
        return price != null ? Money.of(price) : null;
    }

    @Named("moneyToBigDecimal")
    default BigDecimal moneyToBigDecimal(Money money) {
        return money != null ? money.amount() : null;
    }
}
