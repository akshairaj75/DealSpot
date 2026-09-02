package com.backend.dealspot.dto.notification;

import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationRefType;
import com.backend.dealspot.enums.NotificationType;

public class BroadcastNotificationDto {

    private String titleEn;
    private String titleAr;
    private String bodyEn;
    private String bodyAr;
    private NotificationType type = NotificationType.SYSTEM;
    private NotificationChannel channel = NotificationChannel.PUSH;
    private Long refId;
    private NotificationRefType refType;
    private String deepLink;
    private Long targetUserId;

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

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}
