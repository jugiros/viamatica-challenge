package com.viamatica.assessment.orders_management_system.infrastructure.persistence;

import com.viamatica.assessment.orders_management_system.domain.entity.UserDomain;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Email;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.UserEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    @Transactional
    public UserDomain save(UserDomain user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDomain> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDomain> findByEmail(Email email) {
        return jpaRepository.findByEmailAndDeletedAtIsNull(email.value())
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmailAndDeletedAtIsNull(email.value());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserEntity entity = jpaRepository.findById(id).orElse(null);
        if (entity != null) {
            entity.setDeletedAt(java.time.LocalDateTime.now());
            jpaRepository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDomain> findAllActive() {
        return jpaRepository.findByActiveTrueAndDeletedAtIsNull().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private UserDomain toDomain(UserEntity entity) {
        return UserDomain.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(Email.of(entity.getEmail()))
                .passwordHash(entity.getPasswordHash())
                .role(entity.getRole())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity toEntity(UserDomain domain) {
        UserEntity entity = new UserEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail().value());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setRole(domain.getRole());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }
}
