package com.viamatica.assessment.orders_management_system.application.usecase;

import com.viamatica.assessment.orders_management_system.domain.entity.OrderDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.PaymentDomain;
import com.viamatica.assessment.orders_management_system.domain.entity.PaymentMethod;
import com.viamatica.assessment.orders_management_system.domain.exception.DuplicatePaymentException;
import com.viamatica.assessment.orders_management_system.domain.exception.InvalidOrderStateTransitionException;
import com.viamatica.assessment.orders_management_system.domain.exception.OrderNotFoundException;
import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import com.viamatica.assessment.orders_management_system.domain.port.NotificationPort;
import com.viamatica.assessment.orders_management_system.domain.port.OrderRepository;
import com.viamatica.assessment.orders_management_system.domain.port.PaymentRepository;
import com.viamatica.assessment.orders_management_system.domain.order.ConfirmedStatus;
import com.viamatica.assessment.orders_management_system.domain.order.PaidStatus;
import com.viamatica.assessment.orders_management_system.domain.valueobject.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Use case for processing a payment for an order.
 * Validates CONFIRMADA state, checks amount, detects duplicate payments.
 * Transitions to PAGADA and sends notification.
 */
@Service
public class ProcessPaymentUseCase {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final AuditPort auditPort;
    private final NotificationPort notificationPort;

    public ProcessPaymentUseCase(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            AuditPort auditPort,
            NotificationPort notificationPort) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.auditPort = auditPort;
        this.notificationPort = notificationPort;
    }

    public record Command(
            Long orderId,
            PaymentMethod method,
            BigDecimal amount,
            String externalReference
    ) {}

    public PaymentDomain execute(Command command) {
        OrderDomain order = orderRepository.findById(command.orderId)
                .orElseThrow(() -> new OrderNotFoundException(command.orderId));

        if (!(order.getStatus() instanceof ConfirmedStatus)) {
            throw new InvalidOrderStateTransitionException(
                    order.getStatus().name(),
                    "PAGADA"
            );
        }

        if (paymentRepository.existsByOrderId(command.orderId)) {
            throw new DuplicatePaymentException(command.orderId);
        }

        Money paymentAmount = Money.of(command.amount);
        if (!paymentAmount.equals(order.getTotal())) {
            throw new IllegalArgumentException(
                    "Payment amount does not match order total. Expected: " +
                    order.getTotal() + ", Received: " + paymentAmount
            );
        }

        PaymentDomain payment = PaymentDomain.builder()
                .orderId(command.orderId)
                .method(command.method)
                .amount(paymentAmount)
                .externalReference(command.externalReference)
                .build();

        payment.complete();
        PaymentDomain savedPayment = paymentRepository.save(payment);

        order.transitionTo(new PaidStatus());
        orderRepository.save(order);

        auditPort.logEntityChange(
                savedPayment.getId(),
                "payments",
                "INSERT",
                savedPayment.getId(),
                null,
                "{\"orderId\":" + command.orderId + ",\"amount\":" + paymentAmount + "}"
        );

        notificationPort.sendPaymentNotification(
                order.getUserId(),
                order.getId(),
                order.getOrderNumber()
        );

        return savedPayment;
    }
}
