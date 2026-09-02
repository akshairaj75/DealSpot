package com.backend.dealspot.dto.notification;

import com.backend.dealspot.entity.Notification;
import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationRefType;
import com.backend.dealspot.enums.NotificationType;
import java.time.LocalDateTime;

public class NotificationDto {

    private Long id;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private NotificationType type;
    private NotificationChannel channel;
    private String titleEn;
    private String titleAr;
    private String bodyEn;
    private String bodyAr;
    private Long refId;
    private NotificationRefType refType;
    private String deepLink;
    private boolean read;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getBodyEn() {
        return bodyEn;
    }

    public void setBodyEn(String bodyEn) {
        this.bodyEn = bodyEn;
    }

    public String getBodyAr() {
        return bodyAr;
    }

    public void setBodyAr(String bodyAr) {
        this.bodyAr = bodyAr;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public NotificationRefType getRefType() {
        return refType;
    }

    public void setRefType(NotificationRefType refType) {
        this.refType = refType;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static NotificationDto fromEntity(Notification entity) {
        if (entity == null) {
            return null;
        }
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserFullName(entity.getUser().getFullName());
            dto.setUserEmail(entity.getUser().getEmail());
        }
        dto.setType(entity.getType());
        dto.setChannel(entity.getChannel());
        dto.setTitleEn(entity.getTitleEn());
        dto.setTitleAr(entity.getTitleAr());
        dto.setBodyEn(entity.getBodyEn());
        dto.setBodyAr(entity.getBodyAr());
        dto.setRefId(entity.getRefId());
        dto.setRefType(entity.getRefType());
        dto.setDeepLink(entity.getDeepLink());
        dto.setRead(entity.isRead());
        dto.setSentAt(entity.getSentAt());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
