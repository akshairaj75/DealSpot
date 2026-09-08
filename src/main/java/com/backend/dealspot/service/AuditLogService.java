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
    void logAction(
            String entityType,
            Long entityId,
            AuditAction action,
            Map<String, Object> payload);

    void logAction(
            String entityType,
            Long entityId,
            CustomUserPrincipal authUser,
            AuditAction action,
            Map<String, Object> payload,
            HttpServletRequest request);

    void logAction(
            String entityType,
            Long entityId,
            AdminUser performedBy,
            AuditAction action,
            Map<String, Object> payload,
            HttpServletRequest request);

    Page<AuditLogResponseDto> getPagedLogs(AuditLogFilterDto filter);

    List<RecentActivityDto> getRecentActivities(CustomUserPrincipal authUser, int limit);

    List<AuditLogResponseDto> getLogsByEntity(String entityType, Long entityId);

    List<AuditLogResponseDto> getLogsByUser(Long userId);

    List<RecentActivityDto> getAllAuditLogs(CustomUserPrincipal authUser);
}

