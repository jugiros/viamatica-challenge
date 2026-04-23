package com.viamatica.assessment.orders_management_system.infrastructure.persistence.mapper;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "name", target = "name", qualifiedByName = "stringToProductName")
    @Mapping(source = "price", target = "price", qualifiedByName = "bigDecimalToMoney")
    ProductDomain toDomain(ProductEntity entity);

    @Mapping(source = "name.value", target = "name")
    @Mapping(source = "price.amount", target = "price")
    ProductEntity toEntity(ProductDomain domain);

    @Named("stringToProductName")
    default ProductName stringToProductName(String name) {
        if (name == null) {
            return null;
        }
        return ProductName.of(name);
    }

    @Named("bigDecimalToMoney")
    default Money bigDecimalToMoney(BigDecimal price) {
        if (price == null) {
            return null;
        }
        return Money.of(price);
    }
}
