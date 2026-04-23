package com.viamatica.assessment.orders_management_system.infrastructure.persistence;

import com.viamatica.assessment.orders_management_system.domain.entity.ProductDomain;
import com.viamatica.assessment.orders_management_system.domain.port.ProductRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.domain.valueobject.ProductName;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.ProductEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProductRepository port using JPA.
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    @Override
    @Transactional
    public ProductDomain save(ProductDomain product) {
        ProductEntity entity = toEntity(product);
        ProductEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDomain> findById(Long id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDomain> findAllActive() {
        return jpaRepository.findByActiveTrueAndDeletedAtIsNull().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDomain> findByCategoryId(Long categoryId) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getCategoryId() != null && entity.getCategoryId().equals(categoryId))
                .filter(entity -> entity.getDeletedAt() == null)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDomain> findActiveByCategoryId(Long categoryId) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getCategoryId() != null && entity.getCategoryId().equals(categoryId))
                .filter(entity -> entity.getActive() != null && entity.getActive())
                .filter(entity -> entity.getDeletedAt() == null)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDomain> findAll() {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getDeletedAt() == null)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ProductEntity entity = jpaRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setDeletedAt(java.time.LocalDateTime.now());
            jpaRepository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id).isPresent();
    }

    private ProductDomain toDomain(ProductEntity entity) {
        return ProductDomain.builder()
                .id(entity.getId())
                .name(ProductName.of(entity.getName()))
                .description(entity.getDescription())
                .price(Money.of(entity.getPrice()))
                .stock(entity.getStock())
                .categoryId(entity.getCategoryId())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private ProductEntity toEntity(ProductDomain domain) {
        ProductEntity entity = new ProductEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setName(domain.getName().value());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice().amount());
        entity.setStock(domain.getStock());
        entity.setCategoryId(domain.getCategoryId());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }
}
