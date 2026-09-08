package com.backend.dealspot.dto.audit;

import java.time.LocalDateTime;

public class RecentActivityDto {
    private Long auditLogId;
    private Long activityId;
    private String title;
    private String message;
    private String description;
    private String action;
    private String entityType;
    private String performedBy;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime timestamp;

    public Long getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Long auditLogId) {
        this.auditLogId = auditLogId;
        this.activityId = auditLogId;
    }

    public Long getActivityId() {
        return activityId != null ? activityId : auditLogId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
        if (this.auditLogId == null) {
            this.auditLogId = activityId;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.description = message;
    }

    public String getDescription() {
        return description != null ? description : message;
    }

    public void setDescription(String description) {
        this.description = description;
        if (this.message == null) {
            this.message = description;
        }
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        this.timestamp = createdAt;
    }

    public LocalDateTime getTimestamp() {
        return timestamp != null ? timestamp : createdAt;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
        if (this.createdAt == null) {
            this.createdAt = timestamp;
        }
    }
}

