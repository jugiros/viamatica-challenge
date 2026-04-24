package com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user_id", columnList = "user_id"),
    @Index(name = "idx_notification_status", columnList = "estado"),
    @Index(name = "idx_notification_date", columnList = "fecha_envio")
})
@Data
@NoArgsConstructor
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(length = 20)
    private String estado;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaEnvio;

    @PrePersist
    protected void onCreate() {
        if (estado == null) {
            estado = "PENDING";
        }
    }
}
