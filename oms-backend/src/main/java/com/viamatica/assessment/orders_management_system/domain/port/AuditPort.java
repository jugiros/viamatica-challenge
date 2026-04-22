package com.viamatica.assessment.orders_management_system.domain.port;

/**
 * Port for audit logging operations.
 * Pure interface without any Spring or JPA annotations.
 * Implementations will be in the infrastructure layer.
 */
public interface AuditPort {

    /**
     * Logs an audit event.
     * @param userId the user ID (null for system actions)
     * @param table the affected table
     * @param operation the operation performed (INSERT, UPDATE, DELETE, LOGIN, LOGOUT, STATE_CHANGE)
     * @param entityId the affected entity ID
     * @param previousValues the previous values (as JSON)
     * @param newValues the new values (as JSON)
     * @param ipAddress the IP address
     * @param userAgent the user agent string
     */
    void logEvent(Long userId, String table, String operation, Long entityId,
                  String previousValues, String newValues, String ipAddress, String userAgent);

    /**
     * Logs a login event.
     * @param userId the user ID
     * @param ipAddress the IP address
     * @param userAgent the user agent string
     */
    void logLogin(Long userId, String ipAddress, String userAgent);

    /**
     * Logs a logout event.
     * @param userId the user ID
     * @param ipAddress the IP address
     * @param userAgent the user agent string
     */
    void logLogout(Long userId, String ipAddress, String userAgent);

    /**
     * Logs an entity change event.
     * @param userId the user ID
     * @param table the affected table
     * @param operation the operation performed
     * @param entityId the affected entity ID
     * @param previousValues the previous values (as JSON)
     * @param newValues the new values (as JSON)
     */
    void logEntityChange(Long userId, String table, String operation, Long entityId,
                        String previousValues, String newValues);
}
