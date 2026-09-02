package com.backend.dealspot.service;

import com.backend.dealspot.dto.notification.BroadcastNotificationDto;
import com.backend.dealspot.dto.notification.NotificationDto;
import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationType;
import org.springframework.data.domain.Page;

public interface NotificationService {

    NotificationDto sendNotification(Long userId, BroadcastNotificationDto dto);

    int broadcastNotification(BroadcastNotificationDto dto);

    Page<NotificationDto> getUserNotifications(Long userId, int page, int size);

    long getUnreadCount(Long userId);

    boolean markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    Page<NotificationDto> fetchAllNotifications(
            int page,
            int size,
            String search,
            NotificationType type,
            NotificationChannel channel,
            Boolean isRead);

    void deleteNotification(Long id);
}
