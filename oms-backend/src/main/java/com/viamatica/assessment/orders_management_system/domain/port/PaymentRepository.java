package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import java.util.Optional;

/**
 * Repository port for Payment entity.
 * Pure interface without any Spring or JPA annotations.
 * Implementations will be in the infrastructure layer.
 */
public interface PaymentRepository {

    /**
     * Saves a payment entity.
     * @param payment the payment to save
     * @return the saved payment
     */
    PaymentDomain save(PaymentDomain payment);

    /**
     * Finds a payment by ID.
     * @param id the payment ID
     * @return Optional containing the payment if found
     */
    Optional<PaymentDomain> findById(Long id);

    /**
     * Finds a payment by order ID.
     * @param orderId the order ID
     * @return Optional containing the payment if found
     */
    Optional<PaymentDomain> findByOrderId(Long orderId);

    /**
     * Checks if a payment exists for the given order.
     * @param orderId the order ID
     * @return true if a payment exists for the order
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Deletes a payment by ID.
     * @param id the payment ID
     */
    void deleteById(Long id);
}
