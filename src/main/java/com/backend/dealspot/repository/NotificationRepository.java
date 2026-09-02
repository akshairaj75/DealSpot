package com.backend.dealspot.repository;

import com.backend.dealspot.entity.Notification;
import com.backend.dealspot.enums.NotificationChannel;
import com.backend.dealspot.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderBySentAtDesc(Long userId, Pageable pageable);

    long countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId")
    void markAllAsReadForUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id AND n.user.id = :userId")
    int markAsReadForUser(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT n FROM Notification n WHERE " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:channel IS NULL OR n.channel = :channel) AND " +
           "(:isRead IS NULL OR n.read = :isRead) AND " +
           "(:search IS NULL OR :search = '' OR " +
           " LOWER(n.titleEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(n.titleAr) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(n.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(n.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Notification> searchAllNotifications(
            @Param("search") String search,
            @Param("type") NotificationType type,
            @Param("channel") NotificationChannel channel,
            @Param("isRead") Boolean isRead,
            Pageable pageable);
}
