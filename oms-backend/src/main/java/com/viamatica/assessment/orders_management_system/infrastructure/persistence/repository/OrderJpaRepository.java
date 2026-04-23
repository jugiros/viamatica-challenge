package com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository;

import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for OrderEntity.
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByIdAndDeletedAtIsNull(Long id);

    List<OrderEntity> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<OrderEntity> findByOrderNumberAndDeletedAtIsNull(String orderNumber);
}
