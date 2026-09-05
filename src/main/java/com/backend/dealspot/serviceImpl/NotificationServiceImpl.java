package com.backend.dealspot.serviceImpl;

import com.backend.dealspot.dto.notification.BroadcastNotificationDto;
import com.backend.dealspot.dto.notification.NotificationDto;
import com.backend.dealspot.entity.Notification;
import com.backend.dealspot.entity.User;
import com.backend.dealspot.enums.AccountType;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationType;
import com.backend.dealspot.repository.NotificationRepository;
import com.backend.dealspot.repository.StoreFollowRepository;
import com.backend.dealspot.repository.UserRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.NotificationService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final int BATCH_SIZE = 500;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final StoreFollowRepository storeFollowRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            StoreFollowRepository storeFollowRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.storeFollowRepository = storeFollowRepository;
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
    public int broadcastNotification(BroadcastNotificationDto dto, CustomUserPrincipal authUser) {
        if (authUser == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (authUser.getAccountType() != AccountType.ADMIN) {
            throw new AccessDeniedException("Only admin accounts can broadcast notifications");
        }

        // Determine target store scope
        Integer targetStoreId = dto.getStoreId();
        if (authUser.getRole() == AdminRole.STORE_MANAGER) {
            Integer managerStoreId = authUser.getStoreId();
            if (managerStoreId == null) {
                throw new AccessDeniedException("Store manager account is not assigned to any store");
            }
            if (targetStoreId != null && !targetStoreId.equals(managerStoreId)) {
                throw new AccessDeniedException("Store manager can only broadcast notifications to followers of their assigned store");
            }
            targetStoreId = managerStoreId;
        }

        if (dto.getTargetUserId() != null) {
            sendNotification(dto.getTargetUserId(), dto);
            return 1;
        }

        LocalDateTime now = LocalDateTime.now();
        NotificationType type = dto.getType() != null ? dto.getType() : NotificationType.SYSTEM;
        NotificationChannel channel = dto.getChannel() != null ? dto.getChannel() : NotificationChannel.PUSH;
        String titleEn = dto.getTitleEn() != null ? dto.getTitleEn() : "DealSpot Alert";
        String titleAr = dto.getTitleAr() != null ? dto.getTitleAr() : "تنبيه ديل سبوت";

        int totalSent = 0;
        int pageNumber = 0;
        Page<Long> page;

        do {
            if (targetStoreId != null) {
                page = storeFollowRepository.findUserIdsByStoreId(targetStoreId, PageRequest.of(pageNumber, BATCH_SIZE));
            } else {
                page = userRepository.findAllUserIds(PageRequest.of(pageNumber, BATCH_SIZE));
            }

            List<Long> userIds = page.getContent();
            if (userIds.isEmpty()) {
                break;
            }

            List<Notification> batch = new ArrayList<>(userIds.size());
            for (Long userId : userIds) {
                Notification notification = new Notification();
                notification.setUser(entityManager.getReference(User.class, userId));
                notification.setType(type);
                notification.setChannel(channel);
                notification.setTitleEn(titleEn);
                notification.setTitleAr(titleAr);
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
            entityManager.flush();
            entityManager.clear();

            totalSent += batch.size();
            pageNumber++;
        } while (page.hasNext());

        return totalSent;
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
