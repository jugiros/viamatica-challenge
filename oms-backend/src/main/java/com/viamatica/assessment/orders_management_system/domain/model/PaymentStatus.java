package com.viamatica.assessment.orders_management_system.domain.model;

/**
 * Enumeration representing payment status.
 */
public enum PaymentStatus {
    /**
     * Payment is pending processing.
     */
    PENDING,

    /**
     * Payment has been completed successfully.
     */
    COMPLETED,

    /**
     * Payment has failed.
     */
    FAILED,

    /**
     * Payment has been refunded.
     */
    REFUNDED
}
