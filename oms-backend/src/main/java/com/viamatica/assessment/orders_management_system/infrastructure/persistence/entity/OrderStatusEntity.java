package com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity;

/**
 * Enum representing order status in the persistence layer.
 * Used for JPA mapping with @Enumerated(EnumType.STRING).
 */
public enum OrderStatusEntity {
    PENDIENTE,
    CONFIRMADA,
    PAGADA,
    ENVIADA,
    CANCELADA
}
