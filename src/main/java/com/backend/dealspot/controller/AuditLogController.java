package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.audit.AuditLogFilterDto;
import com.backend.dealspot.dto.audit.AuditLogResponseDto;
import com.backend.dealspot.dto.audit.RecentActivityDto;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;

@RestController
@RequestMapping("/api/dealspot/admin/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AuditLogResponseDto>> getPagedLogs(AuditLogFilterDto filter) {
        Page<AuditLogResponseDto> logs = auditLogService.getPagedLogs(filter);
        return ResponseEntity.ok(logs);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('STORE_MANAGER') or hasRole('CONTENT_MANAGER')")
    @GetMapping("/recent")
    public ResponseEntity<List<RecentActivityDto>> getRecentActivities(
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        List<RecentActivityDto> activities = auditLogService.getRecentActivities(authUser, 10);
        return ResponseEntity.ok(activities);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponseDto>> getLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<AuditLogResponseDto> logs = auditLogService.getLogsByEntity(entityType, entityId);
        return ResponseEntity.ok(logs);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponseDto>> getLogsByUser(@PathVariable Long userId) {
        List<AuditLogResponseDto> logs = auditLogService.getLogsByUser(userId);
        return ResponseEntity.ok(logs);
    }
}
