package com.backend.dealspot.serviceImpl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.backend.dealspot.dto.audit.AuditLogFilterDto;
import com.backend.dealspot.dto.audit.AuditLogResponseDto;
import com.backend.dealspot.dto.audit.RecentActivityDto;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.AuditLog;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.repository.AdminUserRepository;
import com.backend.dealspot.repository.AuditLogRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuditServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditLogRepository auditLogRepository;
    private final AdminUserRepository adminUserRepository;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate requiresNewTransactionTemplate;

    public AuditServiceImpl(AuditLogRepository auditLogRepository,
                            AdminUserRepository adminUserRepository,
                            ObjectMapper objectMapper,
                            PlatformTransactionManager transactionManager) {
        this.auditLogRepository = auditLogRepository;
        this.adminUserRepository = adminUserRepository;
        this.objectMapper = objectMapper;
        this.requiresNewTransactionTemplate = new TransactionTemplate(transactionManager);
        this.requiresNewTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public void log(
            String action,
            Long userId,
            String requestId,
            String httpMethod,
            String endpoint,
            Integer statusCode,
            Boolean success,
            String ipAddress,
            String userAgent,
            Long durationMs,
            String errorType,
            String errorMessage) {
        AuditLog auditLog = new AuditLog();

        // Resolve Request ID
        if (requestId == null || requestId.isBlank()) {
            requestId = MDC.get("requestId");
        }
        auditLog.setRequestId(truncate(requestId, 64));

        // Resolve User ID
        if (userId == null) {
            CustomUserPrincipal principal = getCurrentPrincipal();
            if (principal != null && principal.getId() != null) {
                userId = principal.getId();
            } else {
                String mdcUserId = MDC.get("userId");
                if (mdcUserId != null && !mdcUserId.isBlank() && !"anonymous".equalsIgnoreCase(mdcUserId)) {
                    try {
                        userId = Long.parseLong(mdcUserId);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        auditLog.setUserId(userId);

        // Action
        auditLog.setAction(truncate(action != null ? action : "UNKNOWN", 50));

        // Resolve HTTP context
        HttpServletRequest currentRequest = getCurrentRequest();
        if (httpMethod == null && currentRequest != null) {
            httpMethod = currentRequest.getMethod();
        }
        auditLog.setHttpMethod(truncate(httpMethod, 10));

        if (endpoint == null && currentRequest != null) {
            endpoint = currentRequest.getRequestURI();
        }
        auditLog.setEndpoint(truncate(endpoint, 255));

        if (ipAddress == null && currentRequest != null) {
            ipAddress = getClientIpAddress(currentRequest);
        }
        auditLog.setIpAddress(truncate(ipAddress, 45));

        if (userAgent == null && currentRequest != null) {
            userAgent = currentRequest.getHeader("User-Agent");
        }
        auditLog.setUserAgent(truncate(userAgent, 500));

        // Status & Result
        auditLog.setStatusCode(statusCode);
        auditLog.setSuccess(success);
        auditLog.setDurationMs(durationMs);

        // Failure Info
        auditLog.setErrorType(truncate(errorType, 150));
        auditLog.setErrorMessage(truncate(errorMessage, 1000));

        // Entity info fallback
        auditLog.setEntityType(resolveEntityTypeFromAction(action));
        auditLog.setEntityId(0L);

        // Link AdminUser if applicable
        if (userId != null) {
            try {
                adminUserRepository.findById(userId).ifPresent(auditLog::setPerformedBy);
            } catch (Exception ignored) {
            }
        }

        persistAuditLog(auditLog);
    }

    @Override
    public void log(
            AuditAction action,
            Long userId,
            String requestId,
            String httpMethod,
            String endpoint,
            Integer statusCode,
            Boolean success,
            String ipAddress,
            String userAgent,
            Long durationMs,
            String errorType,
            String errorMessage) {
        log(action != null ? action.name() : null, userId, requestId, httpMethod, endpoint, statusCode, success,
                ipAddress, userAgent, durationMs, errorType, errorMessage);
    }

    @Override
    public void log(
            String action,
            boolean success,
            Integer statusCode,
            Long durationMs,
            String errorType,
            String errorMessage) {
        log(action, null, null, null, null, statusCode, success, null, null, durationMs, errorType, errorMessage);
    }

    @Override
    public void log(
            AuditAction action,
            boolean success,
            Integer statusCode,
            Long durationMs,
            String errorType,
            String errorMessage) {
        log(action != null ? action.name() : null, success, statusCode, durationMs, errorType, errorMessage);
    }

    @Override
    public void logAction(
            String entityType,
            Long entityId,
            AuditAction action,
            Map<String, Object> payload) {
        CustomUserPrincipal principal = getCurrentPrincipal();
        HttpServletRequest request = getCurrentRequest();
        logAction(entityType, entityId, principal, action, payload, request);
    }

    @Override
    public void logAction(
            String entityType,
            Long entityId,
            CustomUserPrincipal authUser,
            AuditAction action,
            Map<String, Object> payload,
            HttpServletRequest request) {
        AdminUser admin = null;
        if (authUser != null && authUser.getId() != null) {
            admin = adminUserRepository.findById(authUser.getId()).orElse(null);
        }
        if (admin == null) {
            CustomUserPrincipal current = getCurrentPrincipal();
            if (current != null && current.getId() != null) {
                admin = adminUserRepository.findById(current.getId()).orElse(null);
            }
        }
        if (request == null) {
            request = getCurrentRequest();
        }
        logAction(entityType, entityId, admin, action, payload, request);
    }

    @Override
    public void logAction(
            String entityType,
            Long entityId,
            AdminUser performedBy,
            AuditAction action,
            Map<String, Object> payload,
            HttpServletRequest request) {
        if (performedBy == null) {
            CustomUserPrincipal current = getCurrentPrincipal();
            if (current != null && current.getId() != null) {
                performedBy = adminUserRepository.findById(current.getId()).orElse(null);
            }
        }
        if (request == null) {
            request = getCurrentRequest();
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(truncate(entityType != null ? entityType : "GENERAL", 80));
        auditLog.setEntityId(entityId != null ? entityId : 0L);
        auditLog.setPerformedBy(performedBy);
        auditLog.setAction(action);
        auditLog.setPayload(convertPayloadToJson(payload));
        auditLog.setSuccess(true);
        auditLog.setStatusCode(200);

        // Populate correlation and request info
        String requestId = MDC.get("requestId");
        auditLog.setRequestId(truncate(requestId, 64));

        if (performedBy != null) {
            auditLog.setUserId(performedBy.getId());
        } else {
            CustomUserPrincipal principal = getCurrentPrincipal();
            if (principal != null && principal.getId() != null) {
                auditLog.setUserId(principal.getId());
            }
        }

        if (request != null) {
            auditLog.setHttpMethod(truncate(request.getMethod(), 10));
            auditLog.setEndpoint(truncate(request.getRequestURI(), 255));
            auditLog.setIpAddress(truncate(getClientIpAddress(request), 45));
            auditLog.setUserAgent(truncate(request.getHeader("User-Agent"), 500));
        }

        persistAuditLog(auditLog);
    }

    private void persistAuditLog(AuditLog auditLog) {
        try {
            requiresNewTransactionTemplate.execute(status -> {
                if (auditLog.getEntityType() == null || auditLog.getEntityType().isBlank()) {
                    auditLog.setEntityType(resolveEntityTypeFromAction(auditLog.getAction()));
                }
                if (auditLog.getEntityId() == null) {
                    auditLog.setEntityId(0L);
                }
                return auditLogRepository.save(auditLog);
            });
        } catch (Exception ex) {
            log.error("Failed to persist audit log for action: {}. Reason: {}", auditLog.getAction(), ex.getMessage(), ex);
        }
    }

    @Override
    public Page<AuditLogResponseDto> getPagedLogs(AuditLogFilterDto filter) {
        int page = filter != null && filter.getPage() >= 0 ? filter.getPage() : 0;
        int size = filter != null && filter.getSize() > 0 ? filter.getSize() : 20;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (filter == null) {
            return auditLogRepository.findAll(pageable).map(AuditLogResponseDto::fromEntity);
        }

        String actionStr = filter.getAction() != null ? filter.getAction().name() : null;

        Page<AuditLog> auditLogs = auditLogRepository.searchAuditLogs(
                filter.getEntityType(),
                actionStr,
                filter.getPerformedById(),
                filter.getStartDate(),
                filter.getEndDate(),
                filter.getSearchKeyword() != null ? filter.getSearchKeyword().trim() : null,
                pageable
        );

        return auditLogs.map(AuditLogResponseDto::fromEntity);
    }

    @Override
    public List<RecentActivityDto> getRecentActivities(CustomUserPrincipal authUser, int limit) {
        int size = limit > 0 ? limit : 10;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> page = auditLogRepository.findAll(pageable);
        return page.getContent()
                .stream()
                .map(this::mapToRecentActivity)
                .toList();
    }

    @Override
    public List<RecentActivityDto> getAllAuditLogs(CustomUserPrincipal authUser) {
        List<AuditLog> auditLogs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        return auditLogs
                .stream()
                .map(this::mapToRecentActivity)
                .toList();
    }

    @Override
    public List<AuditLogResponseDto> getLogsByEntity(String entityType, Long entityId) {
        List<AuditLog> auditLogs = auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
        return auditLogs
                .stream()
                .map(AuditLogResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AuditLogResponseDto> getLogsByUser(Long userId) {
        List<AuditLog> auditLogs = auditLogRepository.findByPerformedBy_IdOrderByCreatedAtDesc(userId);
        if (auditLogs.isEmpty()) {
            auditLogs = auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return auditLogs
                .stream()
                .map(AuditLogResponseDto::fromEntity)
                .toList();
    }

    private CustomUserPrincipal getCurrentPrincipal() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal customUser) {
                return customUser;
            }
        } catch (Exception e) {
            log.debug("No authentication principal found in SecurityContext: {}", e.getMessage());
        }
        return null;
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            log.debug("No request context found in RequestContextHolder: {}", e.getMessage());
        }
        return null;
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
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private String truncate(String val, int maxLen) {
        if (val == null) {
            return null;
        }
        return val.length() > maxLen ? val.substring(0, maxLen) : val;
    }

    private String resolveEntityTypeFromAction(String action) {
        if (action == null) {
            return "GENERAL";
        }
        String upper = action.toUpperCase();
        if (upper.contains("USER") || upper.contains("ADMIN") || upper.contains("LOGIN") || upper.contains("LOGOUT")) {
            return "AUTH";
        } else if (upper.contains("OFFER")) {
            return "OFFER";
        } else if (upper.contains("STORE")) {
            return "STORE";
        } else if (upper.contains("FLYER")) {
            return "FLYER";
        } else if (upper.contains("COUPON")) {
            return "COUPON";
        } else if (upper.contains("CATEGORY")) {
            return "CATEGORY";
        } else if (upper.contains("BRAND")) {
            return "BRAND";
        } else if (upper.contains("CITY")) {
            return "CITY";
        } else if (upper.contains("PRODUCT")) {
            return "PRODUCT";
        }
        return "GENERAL";
    }

    private RecentActivityDto mapToRecentActivity(AuditLog auditLog) {
        RecentActivityDto dto = new RecentActivityDto();

        dto.setAuditLogId(auditLog.getId());
        dto.setActivityId(auditLog.getId());
        dto.setCreatedAt(auditLog.getCreatedAt());
        dto.setTimestamp(auditLog.getCreatedAt());
        dto.setAction(auditLog.getAction() != null ? auditLog.getAction() : "");
        dto.setEntityType(auditLog.getEntityType());

        String payload = auditLog.getPayload();
        String entityType = auditLog.getEntityType();
        String userName = auditLog.getPerformedBy() != null
                ? auditLog.getPerformedBy().getFullName()
                : (auditLog.getUserId() != null ? "User #" + auditLog.getUserId() : "System");
        dto.setPerformedBy(userName);

        String entityName = getFormattedEntityName(entityType, payload);
        String typeLabel = formatEntityType(entityType);
        String actionName = auditLog.getAction() != null ? auditLog.getAction().toUpperCase() : "UNKNOWN";

        if (actionName.contains("CREATE") || actionName.equals("CREATE")) {
            dto.setTitle(typeLabel + " Created");
            dto.setColor("success");
            dto.setMessage(entityName + " created by " + userName);
        } else if (actionName.contains("UPDATE") || actionName.equals("UPDATE") || actionName.contains("TOGGLE")) {
            dto.setTitle(typeLabel + " Updated");
            dto.setColor("warning");
            dto.setMessage(entityName + " updated by " + userName);
        } else if (actionName.contains("DELETE") || actionName.equals("DELETE")) {
            dto.setTitle(typeLabel + " Deleted");
            dto.setColor("danger");
            dto.setMessage(entityName + " deleted by " + userName);
        } else if (actionName.contains("APPROVE") || actionName.equals("APPROVE")) {
            dto.setTitle(typeLabel + " Approved");
            dto.setColor("success");
            dto.setMessage(entityName + " approved by " + userName);
        } else if (actionName.contains("REJECT") || actionName.equals("REJECT")) {
            dto.setTitle(typeLabel + " Rejected");
            dto.setColor("danger");
            dto.setMessage(entityName + " rejected by " + userName);
        } else if (actionName.equals("LOGIN")) {
            dto.setTitle("User Login");
            dto.setColor("primary");
            dto.setMessage("Login performed by " + userName);
        } else if (actionName.equals("LOGOUT")) {
            dto.setTitle("User Logout");
            dto.setColor("secondary");
            dto.setMessage("Logout performed by " + userName);
        } else {
            dto.setTitle(actionName);
            dto.setColor("primary");
            dto.setMessage(entityName + " action performed by " + userName);
        }

        return dto;
    }

    private String getFormattedEntityName(String entityType, String payload) {
        String identifier = getEntityIdentifier(entityType, payload);
        String readableType = formatEntityType(entityType);

        if (identifier == null || identifier.isBlank()) {
            return readableType.isEmpty() ? "Entity" : readableType;
        }
        return readableType + " \"" + identifier + "\"";
    }

    private String getEntityIdentifier(String entityType, String payload) {
        if (payload == null || payload.isBlank()) {
            return "";
        }

        String value = "";
        if ("USER".equals(entityType) || "ADMIN_USER".equals(entityType) || "AUTH".equals(entityType)) {
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
