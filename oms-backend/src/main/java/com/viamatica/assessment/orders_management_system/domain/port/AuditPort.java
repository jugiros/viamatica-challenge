package com.viamatica.assessment.orders_management_system.domain.port;

public interface AuditPort {

    void logEvent(Long userId, String table, String operation, Long entityId,
                  String previousValues, String newValues, String ipAddress, String userAgent);

    void logLogin(Long userId, String ipAddress, String userAgent);

    void logLogout(Long userId, String ipAddress, String userAgent);

    void logEntityChange(Long userId, String table, String operation, Long entityId,
                        String previousValues, String newValues);
}
