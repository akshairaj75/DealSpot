package com.backend.dealspot.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.dealspot.config.exceptionsHandler.GlobalExceptionHandler;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.service.AuditLogService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Production-grade Centralized Request Logging Filter.
 * Attaches a unique request/correlation ID and user ID to SLF4J MDC,
 * times the request execution, logs the outcome for every HTTP request,
 * and ensures any unhandled 4xx/5xx HTTP errors are persisted to the audit log.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    public static final String MDC_REQUEST_ID_KEY = "requestId";
    public static final String MDC_TRACE_ID_KEY = "traceId";
    public static final String MDC_USER_ID_KEY = "userId";
    public static final String ANONYMOUS_USER = "anonymous";

    private final ObjectProvider<AuditLogService> auditLogServiceProvider;

    public RequestLoggingFilter(ObjectProvider<AuditLogService> auditLogServiceProvider) {
        this.auditLogServiceProvider = auditLogServiceProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // 1. Resolve or generate unique Request ID
        String requestId = resolveRequestId(request);

        // 2. Populate initial MDC context
        MDC.put(MDC_REQUEST_ID_KEY, requestId);
        MDC.put(MDC_TRACE_ID_KEY, requestId); // For backward compatibility
        MDC.put(MDC_USER_ID_KEY, ANONYMOUS_USER);

        // 3. Attach Request ID to response headers
        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(CORRELATION_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullUri = (queryString != null && !queryString.isBlank()) ? uri + "?" + queryString : uri;

            // Ensure MDC user ID is synchronized with SecurityContext if set later in chain
            syncUserIdWithSecurityContext();

            // Log the completed HTTP request outcome to application log
            log.info("method={} uri={} status={} duration={}ms", method, fullUri, status, duration);

            // If response is an error (status >= 400) and was NOT recorded by GlobalExceptionHandler (e.g. security filter rejection or static 404), persist to audit log table
            if (status >= 400 && !"OPTIONS".equalsIgnoreCase(method)) {
                Object alreadyAudited = request.getAttribute(GlobalExceptionHandler.AUDIT_LOG_RECORDED_ATTR);
                if (alreadyAudited == null) {
                    try {
                        AuditLogService auditService = auditLogServiceProvider.getIfAvailable();
                        if (auditService != null) {
                            String errType = (status >= 500) ? "ServerError" : ((status == 404) ? "NotFound" : ((status == 403) ? "AccessDenied" : ((status == 401) ? "Unauthorized" : "ClientError")));
                            String errMsg = "HTTP " + status + " on " + method + " " + fullUri;
                            auditService.log(
                                    method + " " + (status >= 500 ? "FAILED" : "ERROR"),
                                    null,
                                    requestId,
                                    method,
                                    uri,
                                    status,
                                    false,
                                    null,
                                    null,
                                    duration,
                                    errType,
                                    errMsg
                            );
                        }
                    } catch (Exception e) {
                        log.warn("Failed to record filter audit log for error status {}: {}", status, e.getMessage());
                    }
                }
            }

            // ALWAYS clear MDC to prevent thread-pool context leakage in Tomcat
            MDC.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String reqId = request.getHeader(REQUEST_ID_HEADER);
        if (reqId == null || reqId.isBlank()) {
            reqId = request.getHeader(CORRELATION_ID_HEADER);
        }

        if (reqId != null && !reqId.isBlank()) {
            // Sanitize: allow alphanumeric, hyphens, underscores up to 64 chars
            String cleanId = reqId.trim();
            if (cleanId.length() > 64) {
                cleanId = cleanId.substring(0, 64);
            }
            if (cleanId.matches("^[a-zA-Z0-9_-]+$")) {
                return cleanId;
            }
        }

        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void syncUserIdWithSecurityContext() {
        try {
            String currentUserId = MDC.get(MDC_USER_ID_KEY);
            if (currentUserId == null || currentUserId.isBlank() || ANONYMOUS_USER.equals(currentUserId)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
                    if (principal.getId() != null) {
                        MDC.put(MDC_USER_ID_KEY, String.valueOf(principal.getId()));
                    }
                }
            }
        } catch (Exception ignored) {
            // SecurityContext lookup failure should not affect logging
        }
    }
}
