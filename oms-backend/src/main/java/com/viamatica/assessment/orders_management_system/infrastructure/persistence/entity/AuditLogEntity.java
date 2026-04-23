package com.viamatica.assessment.orders_management_system.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * JPA Entity for Audit Log table.
 * Tracks all changes to entities for audit purposes.
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_table", columnList = "table_name"),
    @Index(name = "idx_audit_date", columnList = "created_at")
})
@Data
@NoArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "operation", nullable = false, length = 50)
    private String operation;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "previous_values", columnDefinition = "TEXT")
    private String previousValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
