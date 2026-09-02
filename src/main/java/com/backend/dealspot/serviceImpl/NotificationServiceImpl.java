package com.backend.dealspot.serviceImpl;

import com.backend.dealspot.dto.notification.BroadcastNotificationDto;
import com.backend.dealspot.dto.notification.NotificationDto;
import com.backend.dealspot.entity.Notification;
import com.backend.dealspot.entity.User;
import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationType;
import com.backend.dealspot.repository.NotificationRepository;
import com.backend.dealspot.repository.UserRepository;
import com.backend.dealspot.service.NotificationService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public NotificationDto sendNotification(Long userId, BroadcastNotificationDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(dto.getType() != null ? dto.getType() : NotificationType.SYSTEM);
        notification.setChannel(dto.getChannel() != null ? dto.getChannel() : NotificationChannel.PUSH);
        notification.setTitleEn(dto.getTitleEn() != null ? dto.getTitleEn() : "DealSpot Alert");
        notification.setTitleAr(dto.getTitleAr() != null ? dto.getTitleAr() : "تنبيه ديل سبوت");
        notification.setBodyEn(dto.getBodyEn());
        notification.setBodyAr(dto.getBodyAr());
        notification.setRefId(dto.getRefId());
        notification.setRefType(dto.getRefType());
        notification.setDeepLink(dto.getDeepLink());
        notification.setRead(false);
        notification.setSentAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return NotificationDto.fromEntity(saved);
    }

    @Override
    public int broadcastNotification(BroadcastNotificationDto dto) {
        if (dto.getTargetUserId() != null) {
            sendNotification(dto.getTargetUserId(), dto);
            return 1;
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Notification> batch = new ArrayList<>(users.size());

        for (User u : users) {
            Notification notification = new Notification();
            notification.setUser(u);
            notification.setType(dto.getType() != null ? dto.getType() : NotificationType.SYSTEM);
            notification.setChannel(dto.getChannel() != null ? dto.getChannel() : NotificationChannel.PUSH);
            notification.setTitleEn(dto.getTitleEn() != null ? dto.getTitleEn() : "DealSpot Alert");
            notification.setTitleAr(dto.getTitleAr() != null ? dto.getTitleAr() : "تنبيه ديل سبوت");
            notification.setBodyEn(dto.getBodyEn());
            notification.setBodyAr(dto.getBodyAr());
            notification.setRefId(dto.getRefId());
            notification.setRefType(dto.getRefType());
            notification.setDeepLink(dto.getDeepLink());
            notification.setRead(false);
            notification.setSentAt(now);
            batch.add(notification);
        }

        notificationRepository.saveAll(batch);
        return batch.size();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId, pageable)
                .map(NotificationDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public boolean markAsRead(Long notificationId, Long userId) {
        int updated = notificationRepository.markAsReadForUser(notificationId, userId);
        return updated > 0;
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> fetchAllNotifications(
            int page,
            int size,
            String search,
            NotificationType type,
            NotificationChannel channel,
            Boolean isRead) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        return notificationRepository.searchAllNotifications(search, type, channel, isRead, pageable)
                .map(NotificationDto::fromEntity);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
