package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import java.util.Optional;

public interface UserRepository {

    UserDomain save(UserDomain user);

    Optional<UserDomain> findById(Long id);

    Optional<UserDomain> findByEmail(Email email);

    boolean existsByEmail(Email email);

    void deleteById(Long id);

    java.util.List<UserDomain> findAllActive();
}
