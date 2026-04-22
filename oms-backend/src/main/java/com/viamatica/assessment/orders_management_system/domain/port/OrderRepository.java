package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import java.util.List;
import java.util.Optional;

/**
 * Repository port for Order entity.
 * Pure interface without any Spring or JPA annotations.
 * Implementations will be in the infrastructure layer.
 */
public interface OrderRepository {

    /**
     * Saves an order entity.
     * @param order the order to save
     * @return the saved order
     */
    OrderDomain save(OrderDomain order);

    /**
     * Finds an order by ID.
     * @param id the order ID
     * @return Optional containing the order if found
     */
    Optional<OrderDomain> findById(Long id);

    /**
     * Finds an order by order number.
     * @param orderNumber the order number
     * @return Optional containing the order if found
     */
    Optional<OrderDomain> findByOrderNumber(String orderNumber);

    /**
     * Finds all orders for a specific user.
     * @param userId the user ID
     * @return list of orders for the user
     */
    List<OrderDomain> findByUserId(Long userId);

    /**
     * Finds all orders.
     * @return list of all orders
     */
    List<OrderDomain> findAll();

    /**
     * Deletes an order by ID.
     * @param id the order ID
     */
    void deleteById(Long id);

    /**
     * Checks if an order exists with the given ID.
     * @param id the order ID
     * @return true if the order exists
     */
    boolean existsById(Long id);
}
