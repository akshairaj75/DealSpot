package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
