package com.viamatica.assessment.orders_management_system.infrastructure.service;

import com.viamatica.assessment.orders_management_system.domain.port.AuditPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of AuditPort for logging audit events.
 */
@Service
@Slf4j
public class AuditPortImpl implements AuditPort {

    @Override
    public void logEntityChange(Long entityId, String entityName, String action, Long userId, String oldValue, String newValue) {
        log.info("Audit Log - Entity: {}, Action: {}, EntityId: {}, UserId: {}, Old: {}, New: {}, Timestamp: {}",
                entityName, action, entityId, userId, oldValue, newValue, LocalDateTime.now());
    }

    @Override
    public void logLogin(Long userId, String ipAddress, String userAgent) {
        log.info("Audit Log - LOGIN - UserId: {}, IP: {}, UserAgent: {}, Timestamp: {}",
                userId, ipAddress, userAgent, LocalDateTime.now());
    }

    @Override
    public void logLogout(Long userId, String ipAddress, String userAgent) {
        log.info("Audit Log - LOGOUT - UserId: {}, IP: {}, UserAgent: {}, Timestamp: {}",
                userId, ipAddress, userAgent, LocalDateTime.now());
    }

    @Override
    public void logEvent(Long userId, String table, String operation, Long entityId,
                         String previousValues, String newValues, String ipAddress, String userAgent) {
        log.info("Audit Log - Event - UserId: {}, Table: {}, Operation: {}, EntityId: {}, IP: {}, UserAgent: {}, Timestamp: {}",
                userId, table, operation, entityId, ipAddress, userAgent, LocalDateTime.now());
    }
}
