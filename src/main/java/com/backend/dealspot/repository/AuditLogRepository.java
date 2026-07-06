package com.backend.dealspot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.dealspot.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
