package com.backend.dealspot.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.backend.dealspot.dto.audit.AuditLogFilterDto;
import com.backend.dealspot.dto.audit.AuditLogResponseDto;
import com.backend.dealspot.dto.audit.RecentActivityDto;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.security.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;

public interface AuditLogService {

    /**
     * Primary audit logging API with explicit parameters.
     */
    void log(
            String action,
            Long userId,
            String requestId,
            String httpMethod,
            String endpoint,
            Integer statusCode,
            Boolean success,
            String ipAddress,
            String userAgent,
            Long durationMs,
            String errorType,
            String errorMessage
    );

    /**
     * Overload accepting AuditAction enum.
     */
    void log(
            AuditAction action,
            Long userId,
            String requestId,
            String httpMethod,
            String endpoint,
            Integer statusCode,
            Boolean success,
            String ipAddress,
            String userAgent,
            Long durationMs,
            String errorType,
            String errorMessage
    );

    /**
     * Convenience audit logging that auto-resolves requestId, userId, httpMethod, endpoint, IP, and userAgent from context.
     */
    void log(
            String action,
            boolean success,
            Integer statusCode,
            Long durationMs,
            String errorType,
            String errorMessage
    );

    /**
     * Convenience audit logging accepting AuditAction enum that auto-resolves context.
     */
    void log(
            AuditAction action,
            boolean success,
            Integer statusCode,
            Long durationMs,
            String errorType,
            String errorMessage
    );

    /**
     * Entity-level action logging with auto-resolved context.
     */
    void logAction(
            String entityType,
            Long entityId,
            AuditAction action,
            Map<String, Object> payload
    );

    /**
     * Entity-level action logging with specified principal and request.
     */
    void logAction(
            String entityType,
            Long entityId,
            CustomUserPrincipal authUser,
            AuditAction action,
            Map<String, Object> payload,
            HttpServletRequest request
    );

    /**
     * Entity-level action logging with specified AdminUser entity and request.
     */
    void logAction(
            String entityType,
            Long entityId,
            AdminUser performedBy,
            AuditAction action,
            Map<String, Object> payload,
            HttpServletRequest request
    );

    Page<AuditLogResponseDto> getPagedLogs(AuditLogFilterDto filter);

    List<RecentActivityDto> getRecentActivities(CustomUserPrincipal authUser, int limit);

    List<AuditLogResponseDto> getLogsByEntity(String entityType, Long entityId);

    List<AuditLogResponseDto> getLogsByUser(Long userId);

    List<RecentActivityDto> getAllAuditLogs(CustomUserPrincipal authUser);
}
