package com.viamatica.assessment.orders_management_system.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Logs", description = "Audit log endpoints (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {

    @GetMapping
    @Operation(summary = "Get audit logs", description = "Retrieve audit logs with optional filters")
    public String getAuditLogs(
            @Parameter(description = "Filter by user ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filter by table name") @RequestParam(required = false) String table,
            @Parameter(description = "Filter by operation type") @RequestParam(required = false) String operation,
            @Parameter(description = "Filter by date from") @RequestParam(required = false) String fechaDesde,
            @Parameter(description = "Filter by date to") @RequestParam(required = false) String fechaHasta,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        return "Audit logs endpoint - to be implemented with filters";
    }
}
