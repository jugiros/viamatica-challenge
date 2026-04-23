package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import java.util.Optional;

public interface PaymentRepository {

    PaymentDomain save(PaymentDomain payment);

    Optional<PaymentDomain> findById(Long id);

    Optional<PaymentDomain> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    void deleteById(Long id);
}
