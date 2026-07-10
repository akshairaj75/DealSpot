package com.backend.dealspot.dto.audit;

import java.time.LocalDateTime;

import com.backend.dealspot.dto.auth.UserResponseDto;
import com.backend.dealspot.entity.AuditLog;
import com.backend.dealspot.enums.AuditAction;

public class AuditLogResponseDto {
    private Long auditLogId;
    private String entityType;
    private Long entityId;
    private UserResponseDto performedBy;
    private String payload;
    private AuditAction action;
    private String ipAddress; 
    private LocalDateTime createdAt;

    public Long getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Long auditLogId) {
        this.auditLogId = auditLogId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public UserResponseDto getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(UserResponseDto performedBy) {
        this.performedBy = performedBy;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public static AuditLogResponseDto fromEntity(AuditLog auditLog) {
        AuditLogResponseDto dto = new AuditLogResponseDto();
        dto.setAuditLogId(auditLog.getId());
        dto.setEntityType(auditLog.getEntityType());
        dto.setEntityId(auditLog.getEntityId());
        if (auditLog.getPerformedBy() != null) {
            dto.setPerformedBy(UserResponseDto.fromEntity(auditLog.getPerformedBy()));
        }
        dto.setPayload(auditLog.getPayload());
        dto.setAction(auditLog.getAction());
        dto.setIpAddress(auditLog.getIpAddress());
        dto.setCreatedAt(auditLog.getCreatedAt());
        return dto;
    }

}
