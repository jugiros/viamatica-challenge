package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.exception.UserNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrdersByUserUseCase {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public record Query(
            Long userId,
            String status,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            int page,
            int size
    ) {}

    public List<OrderDomain> execute(Query query) {
        if (userRepository.findById(query.userId).isEmpty()) {
            throw new UserNotFoundException(query.userId);
        }

        List<OrderDomain> orders = orderRepository.findByUserId(query.userId);

        if (query.status != null) {
            orders = orders.stream()
                    .filter(order -> order.getStatus().name().equals(query.status))
                    .toList();
        }

        if (query.dateFrom != null) {
            orders = orders.stream()
                    .filter(order -> order.getCreatedAt().isAfter(query.dateFrom) ||
                                   order.getCreatedAt().isEqual(query.dateFrom))
                    .toList();
        }

        if (query.dateTo != null) {
            orders = orders.stream()
                    .filter(order -> order.getCreatedAt().isBefore(query.dateTo) ||
                                   order.getCreatedAt().isEqual(query.dateTo))
                    .toList();
        }

        int fromIndex = query.page * query.size;
        if (fromIndex >= orders.size()) {
            return List.of();
        }

        int toIndex = Math.min(fromIndex + query.size, orders.size());
        return orders.subList(fromIndex, toIndex);
    }
}
