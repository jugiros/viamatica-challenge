package com.viamatica.assessment.orders_management_system.infrastructure.persistence;

import com.viamatica.assessment.orders_management_system.domain.entity.CategoryDomain;
import com.viamatica.assessment.orders_management_system.domain.port.CategoryRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.CategoryName;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.CategoryEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    @Transactional
    public CategoryDomain save(CategoryDomain category) {
        CategoryEntity entity = toEntity(category);
        CategoryEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryDomain> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDomain> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoryDomain> findByName(CategoryName name) {
        return jpaRepository.findByName(name.value()).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return jpaRepository.findById(id).isPresent();
    }

    private CategoryDomain toDomain(CategoryEntity entity) {
        return CategoryDomain.builder()
                .id(entity.getId())
                .name(CategoryName.of(entity.getName()))
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private CategoryEntity toEntity(CategoryDomain domain) {
        CategoryEntity entity = new CategoryEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setName(domain.getName().value());
        entity.setDescription(domain.getDescription());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}
