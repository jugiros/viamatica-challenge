package com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository;

import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ProductEntity.
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long id);

    List<ProductEntity> findByActiveTrueAndDeletedAtIsNull();

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<ProductEntity> findByIdAndDeletedAtIsNullAndActiveTrue(Long id);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.category WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<ProductEntity> findByIdWithCategory(@Param("id") Long id);
}
