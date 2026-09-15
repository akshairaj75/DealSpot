package com.backend.dealspot.dto.audit;

import java.time.LocalDateTime;

import com.backend.dealspot.dto.auth.UserResponseDto;
import com.backend.dealspot.entity.AuditLog;

public class AuditLogResponseDto {
    private Long auditLogId;
    private String requestId;
    private Long userId;
    private String action;
    private String httpMethod;
    private String endpoint;
    private Integer statusCode;
    private Boolean success;
    private String ipAddress;
    private String userAgent;
    private Long durationMs;
    private String errorType;
    private String errorMessage;
    private String entityType;
    private Long entityId;
    private UserResponseDto performedBy;
    private String payload;
    private LocalDateTime createdAt;

    public AuditLogResponseDto() {
    }

    public Long getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Long auditLogId) {
        this.auditLogId = auditLogId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static AuditLogResponseDto fromEntity(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        AuditLogResponseDto dto = new AuditLogResponseDto();
        dto.setAuditLogId(auditLog.getId());
        dto.setRequestId(auditLog.getRequestId());
        dto.setUserId(auditLog.getUserId());
        dto.setAction(auditLog.getAction());
        dto.setHttpMethod(auditLog.getHttpMethod());
        dto.setEndpoint(auditLog.getEndpoint());
        dto.setStatusCode(auditLog.getStatusCode());
        dto.setSuccess(auditLog.getSuccess());
        dto.setIpAddress(auditLog.getIpAddress());
        dto.setUserAgent(auditLog.getUserAgent());
        dto.setDurationMs(auditLog.getDurationMs());
        dto.setErrorType(auditLog.getErrorType());
        dto.setErrorMessage(auditLog.getErrorMessage());
        dto.setEntityType(auditLog.getEntityType());
        dto.setEntityId(auditLog.getEntityId());
        if (auditLog.getPerformedBy() != null) {
            dto.setPerformedBy(UserResponseDto.fromEntity(auditLog.getPerformedBy()));
        }
        dto.setPayload(auditLog.getPayload());
        dto.setCreatedAt(auditLog.getCreatedAt());
        return dto;
    }
}
