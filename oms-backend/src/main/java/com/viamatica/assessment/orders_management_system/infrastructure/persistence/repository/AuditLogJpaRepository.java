package com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository;

import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for AuditLogEntity.
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {

    @Query("SELECT a FROM AuditLogEntity a WHERE a.userId = :userId AND a.createdAt BETWEEN :dateFrom AND :dateTo ORDER BY a.createdAt DESC")
    Page<AuditLogEntity> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );

    @Query("SELECT a FROM AuditLogEntity a WHERE a.tableName = :tableName AND a.createdAt BETWEEN :dateFrom AND :dateTo ORDER BY a.createdAt DESC")
    Page<AuditLogEntity> findByTableNameAndDateRange(
            @Param("tableName") String tableName,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );

    @Query("SELECT a FROM AuditLogEntity a WHERE a.userId = :userId AND a.tableName = :tableName AND a.createdAt BETWEEN :dateFrom AND :dateTo ORDER BY a.createdAt DESC")
    Page<AuditLogEntity> findByUserIdAndTableNameAndDateRange(
            @Param("userId") Long userId,
            @Param("tableName") String tableName,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable
    );

    List<AuditLogEntity> findByEntityIdOrderByCreatedAtDesc(Long entityId);
}
