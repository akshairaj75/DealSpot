package com.backend.dealspot.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dealspot.dto.audit.AuditLogResponseDto;
import com.backend.dealspot.dto.audit.RecentActivityDto;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.AuditLog;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.repository.AuditLogRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuditServiceImpl implements AuditLogService {

    @Autowired
    AuditLogRepository auditLogRepository;

    private final ObjectMapper objectMapper;

    public AuditServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // @Override
    // public List<AuditLogResponseDto> getAllAuditLogs(CustomUserPrincipal
    // authUser) {
    // List<AuditLog> auditLogs = auditLogRepository.findAll();
    // return auditLogs
    // .stream()
    // .map(AuditLogResponseDto::fromEntity)
    // .toList();
    // }

    @Override
    public List<RecentActivityDto> getAllAuditLogs(CustomUserPrincipal authUser) {

        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        return auditLogs
                .stream()
                .map(this::mapToRecentActivity)
                .toList();

    }

    @Transactional
    @Override
    public void logAction(
            String entityType,
            Long entityId,
            AdminUser performedBy,
            AuditAction action,
            Map<String, Object> payload,
            HttpServletRequest request) {

        AuditLog auditLog = new AuditLog();

        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setPerformedBy(performedBy);
        auditLog.setAction(action);
        auditLog.setPayload(convertPayloadToJson(payload));
        auditLog.setIpAddress(getClientIpAddress(request));

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogResponseDto> getLogsByEntity(String entityType, Long entityId) {

        List<AuditLog> auditLogs = auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType,
                entityId);
        return auditLogs
                .stream()
                .map(AuditLogResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AuditLogResponseDto> getLogsByUser(Long userId) {

        return auditLogRepository.findByPerformedBy_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(AuditLogResponseDto::fromEntity)
                .toList();
    }

    private String convertPayloadToJson(Map<String, Object> payload) {

        if (payload == null || payload.isEmpty()) {
            return "{}";
        }

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to convert payload to JSON\"}";
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {

        if (request == null) {
            return null;
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        return request.getRemoteAddr();
    }

    private RecentActivityDto mapToRecentActivity(AuditLog log) {

        RecentActivityDto dto = new RecentActivityDto();

        dto.setAuditLogId(log.getId());
        dto.setCreatedAt(log.getCreatedAt());

        String payload = log.getPayload();
        String entityType = log.getEntityType();
        String userName = log.getPerformedBy() != null ? log.getPerformedBy().getFullName() : "Someone";
        
        String entityName = getFormattedEntityName(entityType, payload);
        String typeLabel = formatEntityType(entityType);

        switch (log.getAction()) {
            case CREATE -> {
                dto.setTitle(typeLabel + " Created");
                dto.setColor("success");
                dto.setMessage(entityName + " created by " + userName);
            }
            case UPDATE -> {
                dto.setTitle(typeLabel + " Updated");
                dto.setColor("warning");
                dto.setMessage(entityName + " updated by " + userName);
            }
            case DELETE -> {
                dto.setTitle(typeLabel + " Deleted");
                dto.setColor("danger");
                dto.setMessage(entityName + " deleted by " + userName);
            }
            case APPROVE -> {
                dto.setTitle(typeLabel + " Approved");
                dto.setColor("success");
                dto.setMessage(entityName + " approved by " + userName);
            }
            case REJECT -> {
                dto.setTitle(typeLabel + " Rejected");
                dto.setColor("danger");
                dto.setMessage(entityName + " rejected by " + userName);
            }
            case BULK_EXPIRE -> {
                dto.setTitle(typeLabel + " Bulk Expired");
                dto.setColor("warning");
                dto.setMessage("Bulk expire performed on " + entityName + " by " + userName);
            }
            default -> {
                dto.setTitle(log.getAction().name());
                dto.setColor("primary");
                dto.setMessage(entityName + " action performed by " + userName);
            }
        }

        return dto;
    }

    private String getFormattedEntityName(String entityType, String payload) {
        String identifier = getEntityIdentifier(entityType, payload);
        String readableType = formatEntityType(entityType);
        
        if (identifier == null || identifier.isBlank()) {
            return readableType;
        }
        return readableType + " \"" + identifier + "\"";
    }

    private String getEntityIdentifier(String entityType, String payload) {
        if (payload == null || payload.isBlank()) {
            return "";
        }
        
        String value = "";
        if ("USER".equals(entityType) || "ADMIN_USER".equals(entityType)) {
            value = readPayloadValue(payload, "email");
            if (value.isBlank()) {
                value = readPayloadValue(payload, "fullName");
            }
        } else if ("COUPON_CODE".equals(entityType)) {
            value = readPayloadValue(payload, "code");
        } else if ("FLYER".equals(entityType) || "OFFER".equals(entityType)) {
            value = readPayloadValue(payload, "titleEn");
            if (value.isBlank()) {
                value = readPayloadValue(payload, "titleAr");
            }
            if (value.isBlank()) {
                value = readPayloadValue(payload, "title");
            }
        } else {
            value = readPayloadValue(payload, "nameEn");
            if (value.isBlank()) {
                value = readPayloadValue(payload, "nameAr");
            }
            if (value.isBlank()) {
                value = readPayloadValue(payload, "name");
            }
        }
        
        return value;
    }

    private String formatEntityType(String entityType) {
        if (entityType == null) {
            return "";
        }
        String[] words = entityType.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String readPayloadValue(String payload, String key) {

        if (payload == null || payload.isBlank()) {
            return "";
        }

        try {
            JsonNode node = objectMapper.readTree(payload);

            if (node.has(key) && !node.get(key).isNull()) {
                return node.get(key).asText();
            }

            return "";

        } catch (Exception e) {
            return "";
        }
    }

}
