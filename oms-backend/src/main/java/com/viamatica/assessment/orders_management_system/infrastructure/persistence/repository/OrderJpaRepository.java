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

    Optional<OrderEntity> findById(Long id);

    List<OrderEntity> findByUserId(Long userId);

    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    Optional<OrderEntity> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT o FROM OrderEntity o WHERE o.userId = :userId AND o.status = :status AND o.createdAt BETWEEN :dateFrom AND :dateTo")
    Page<OrderEntity> findByUserIdAndStatusAndDateRange(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );
}
