package com.viamatica.assessment.orders_management_system.domain.port;

public interface NotificationPort {

    void sendOrderConfirmation(Long userId, Long orderId, String orderNumber);

    void sendPaymentNotification(Long userId, Long orderId, String orderNumber);

    void sendShippingNotification(Long userId, Long orderId, String orderNumber);

    void sendCancellationNotification(Long userId, Long orderId, String orderNumber, String reason);

    void sendCustomNotification(Long userId, Long orderId, String type, String message);
}
