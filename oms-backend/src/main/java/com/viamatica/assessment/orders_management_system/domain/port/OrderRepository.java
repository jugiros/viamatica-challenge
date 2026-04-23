package com.viamatica.assessment.orders_management_system.domain.port;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    OrderDomain save(OrderDomain order);

    Optional<OrderDomain> findById(Long id);

    Optional<OrderDomain> findByOrderNumber(String orderNumber);

    List<OrderDomain> findByUserId(Long userId);

    List<OrderDomain> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
