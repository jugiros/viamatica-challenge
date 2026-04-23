package com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository;

import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByIdAndDeletedAtIsNull(Long id);

    List<OrderEntity> findByUserIdAndDeletedAtIsNull(Long userId);

    Page<OrderEntity> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    Optional<OrderEntity> findByOrderNumberAndDeletedAtIsNull(String orderNumber);

    Optional<OrderEntity> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);

    @Query("SELECT o FROM OrderEntity o WHERE o.userId = :userId AND o.deletedAt IS NULL AND o.status = :status AND o.orderDate BETWEEN :dateFrom AND :dateTo")
    Page<OrderEntity> findByUserIdAndStatusAndDateRange(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );
}
