package com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository;

import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for PaymentEntity.
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByIdAndDeletedAtIsNull(Long id);

    Optional<PaymentEntity> findByOrderIdAndDeletedAtIsNull(Long orderId);

    boolean existsByOrderIdAndDeletedAtIsNull(Long orderId);
}
