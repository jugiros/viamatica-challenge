package com.viamatica.assessment.orders_management_system.infrastructure.persistence;

import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentMethod;
import com.viamatica.assessment.orders_management_system.domain.model.PaymentStatus;
import com.viamatica.assessment.orders_management_system.domain.port.PaymentRepository;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity.PaymentEntity;
import com.viamatica.assessment.orders_management_system.infrastructure.persistence.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    @Transactional
    public PaymentDomain save(PaymentDomain payment) {
        PaymentEntity entity = toEntity(payment);
        PaymentEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDomain> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDomain> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId)
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrderId(Long orderId) {
        return jpaRepository.existsByOrderId(orderId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private PaymentDomain toDomain(PaymentEntity entity) {
        return PaymentDomain.builder()
                .id(entity.getId())
                .orderId(entity.getOrderId())
                .method(entity.getMethod())
                .amount(Money.of(entity.getAmount()))
                .status(entity.getStatus())
                .externalReference(entity.getExternalReference())
                .paymentDate(entity.getPaymentDate())
                .build();
    }

    private PaymentEntity toEntity(PaymentDomain domain) {
        PaymentEntity entity = new PaymentEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setOrderId(domain.getOrderId());
        entity.setMethod(domain.getMethod());
        entity.setAmount(domain.getAmount().amount());
        entity.setStatus(domain.getStatus());
        entity.setExternalReference(domain.getExternalReference());
        entity.setPaymentDate(domain.getPaymentDate());
        return entity;
    }
}
