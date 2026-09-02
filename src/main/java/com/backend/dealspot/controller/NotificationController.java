package com.backend.dealspot.controller;

import com.backend.dealspot.dto.notification.BroadcastNotificationDto;
import com.backend.dealspot.dto.notification.NotificationDto;
import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationType;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.NotificationService;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dealspot/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/my")
    public ResponseEntity<Page<NotificationDto>> getMyNotifications(
            @AuthenticationPrincipal CustomUserPrincipal authUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        Page<NotificationDto> notifications = notificationService.getUserNotifications(authUser.getId(), page, size);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        if (authUser == null) {
            return ResponseEntity.ok(Map.of("unreadCount", 0));
        }
        long count = notificationService.getUnreadCount(authUser.getId());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        boolean success = notificationService.markAsRead(id, authUser.getId());
        return ResponseEntity.ok(Map.of("success", success));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @AuthenticationPrincipal CustomUserPrincipal authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllAsRead(authUser.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<NotificationDto>> getAllNotifications(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "type", required = false) NotificationType type,
            @RequestParam(name = "channel", required = false) NotificationChannel channel,
            @RequestParam(name = "isRead", required = false) Boolean isRead) {
        Page<NotificationDto> result = notificationService.fetchAllNotifications(page, size, search, type, channel, isRead);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcastNotification(
            @RequestBody BroadcastNotificationDto dto) {
        int count = notificationService.broadcastNotification(dto);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "sentCount", count,
                "message", "Notification broadcast sent successfully to " + count + " recipient(s)."
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Notification deleted."));
    }
}
