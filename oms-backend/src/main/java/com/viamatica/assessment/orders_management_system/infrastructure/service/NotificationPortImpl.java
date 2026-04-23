package com.viamatica.assessment.orders_management_system.infrastructure.service;

import com.viamatica.assessment.orders_management_system.domain.port.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationPortImpl implements NotificationPort {

    @Override
    public void sendOrderConfirmation(Long userId, Long orderId, String orderNumber) {
        log.info("Order Confirmation Notification - UserId: {}, OrderId: {}, OrderNumber: {}, Timestamp: {}",
                userId, orderId, orderNumber, java.time.LocalDateTime.now());
    }

    @Override
    public void sendPaymentNotification(Long userId, Long orderId, String orderNumber) {
        log.info("Payment Notification - UserId: {}, OrderId: {}, OrderNumber: {}, Timestamp: {}",
                userId, orderId, orderNumber, java.time.LocalDateTime.now());
    }

    @Override
    public void sendShippingNotification(Long userId, Long orderId, String orderNumber) {
        log.info("Shipping Notification - UserId: {}, OrderId: {}, OrderNumber: {}, Timestamp: {}",
                userId, orderId, orderNumber, java.time.LocalDateTime.now());
    }

    @Override
    public void sendCancellationNotification(Long userId, Long orderId, String orderNumber, String reason) {
        log.info("Cancellation Notification - UserId: {}, OrderId: {}, OrderNumber: {}, Reason: {}, Timestamp: {}",
                userId, orderId, orderNumber, reason, java.time.LocalDateTime.now());
    }

    @Override
    public void sendCustomNotification(Long userId, Long orderId, String type, String message) {
        log.info("Custom Notification - UserId: {}, OrderId: {}, Type: {}, Message: {}, Timestamp: {}",
                userId, orderId, type, message, java.time.LocalDateTime.now());
    }
}
