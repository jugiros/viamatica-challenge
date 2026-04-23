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

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByActiveTrue();

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<ProductEntity> findByIdAndActiveTrue(Long id);

    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithCategory(@Param("id") Long id);
}
