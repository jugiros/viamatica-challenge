package com.viamatica.assessment.orders_management_system.infrastructure.persistence.mapper;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.model.UserRole;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "email", qualifiedByName = "stringToEmail")
    UserDomain toDomain(UserEntity entity);

    @Mapping(target = "email", qualifiedByName = "emailToString")
    UserEntity toEntity(UserDomain domain);

    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? new Email(email) : null;
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.value() : null;
    }
}
