package com.viamatica.assessment.orders_management_system.domain.port;

/**
 * Port for notification operations.
 * Pure interface without any Spring or JPA annotations.
 * Implementations will be in the infrastructure layer.
 */
public interface NotificationPort {

    /**
     * Sends a confirmation notification for an order.
     * @param userId the user ID
     * @param orderId the order ID
     * @param orderNumber the order number
     */
    void sendOrderConfirmation(Long userId, Long orderId, String orderNumber);

    /**
     * Sends a payment notification for an order.
     * @param userId the user ID
     * @param orderId the order ID
     * @param orderNumber the order number
     */
    void sendPaymentNotification(Long userId, Long orderId, String orderNumber);

    /**
     * Sends a shipping notification for an order.
     * @param userId the user ID
     * @param orderId the order ID
     * @param orderNumber the order number
     */
    void sendShippingNotification(Long userId, Long orderId, String orderNumber);

    /**
     * Sends a cancellation notification for an order.
     * @param userId the user ID
     * @param orderId the order ID
     * @param orderNumber the order number
     * @param reason the cancellation reason
     */
    void sendCancellationNotification(Long userId, Long orderId, String orderNumber, String reason);

    /**
     * Sends a custom notification.
     * @param userId the user ID
     * @param orderId the order ID
     * @param type the notification type
     * @param message the notification message
     */
    void sendCustomNotification(Long userId, Long orderId, String type, String message);
}
