package com.backend.dealspot.entity;

import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationRefType;
import com.backend.dealspot.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notif_user", columnList = "user_id"),
        @Index(name = "idx_notif_type", columnList = "type"),
        @Index(name = "idx_notif_channel", columnList = "channel"),
        @Index(name = "idx_notif_read", columnList = "is_read"),
        @Index(name = "idx_notif_sent", columnList = "sent_at")
    }
)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type = NotificationType.SYSTEM;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 10)
    private NotificationChannel channel = NotificationChannel.PUSH;

    @Column(name = "title_en", nullable = false, length = 200)
    private String titleEn;

    @Column(name = "title_ar", nullable = false, length = 200)
    private String titleAr;

    @Column(name = "body_en", columnDefinition = "TEXT")
    private String bodyEn;

    @Column(name = "body_ar", columnDefinition = "TEXT")
    private String bodyAr;

    @Column(name = "ref_id")
    private Long refId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", length = 20)
    private NotificationRefType refType;

    @Column(name = "deep_link", length = 255)
    private String deepLink;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
