package com.backend.dealspot.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.dealspot.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByCreatedAtDesc();

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);

    List<AuditLog> findByPerformedBy_IdOrderByCreatedAtDesc(Long userId);

    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<AuditLog> findByRequestIdOrderByCreatedAtDesc(String requestId);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityType IS NULL OR :entityType = '' OR a.entityType = :entityType) AND " +
           "(:action IS NULL OR :action = '' OR a.action = :action) AND " +
           "(:performedById IS NULL OR (a.performedBy IS NOT NULL AND a.performedBy.id = :performedById) OR a.userId = :performedById) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) AND " +
           "(:search IS NULL OR :search = '' OR " +
           " (a.requestId IS NOT NULL AND LOWER(a.requestId) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
           " (a.endpoint IS NOT NULL AND LOWER(a.endpoint) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
           " (a.action IS NOT NULL AND LOWER(a.action) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
           " (a.entityType IS NOT NULL AND LOWER(a.entityType) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
           " (a.performedBy IS NOT NULL AND LOWER(a.performedBy.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
           " (a.performedBy IS NOT NULL AND LOWER(a.performedBy.email) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
           " (a.payload IS NOT NULL AND LOWER(a.payload) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<AuditLog> searchAuditLogs(
            @Param("entityType") String entityType,
            @Param("action") String action,
            @Param("performedById") Long performedById,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("search") String search,
            Pageable pageable);
}
