package com.backend.dealspot.config.exceptionsHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.backend.dealspot.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public static final String AUDIT_LOG_RECORDED_ATTR = "AUDIT_LOG_RECORDED";

    private final AuditLogService auditLogService;

    public GlobalExceptionHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    private void recordAuditError(String action, int statusCode, Exception ex, HttpServletRequest request) {
        try {
            if (request != null) {
                request.setAttribute(AUDIT_LOG_RECORDED_ATTR, Boolean.TRUE);
            }
            String errorType = ex.getClass().getSimpleName();
            String errorMessage = ex.getMessage();
            auditLogService.log(
                    action != null ? action : "HTTP_ERROR",
                    null,
                    null,
                    request != null ? request.getMethod() : null,
                    request != null ? request.getRequestURI() : null,
                    statusCode,
                    false,
                    null,
                    null,
                    null,
                    errorType,
                    errorMessage);
        } catch (Exception e) {
            log.warn("Failed to write database audit log for error {}: {}", ex.getClass().getSimpleName(),
                    e.getMessage());
        }
    }

    @ExceptionHandler(com.backend.dealspot.exception.OfferConflictException.class)
    public ResponseEntity<com.backend.dealspot.dto.offer.OfferConflictResponseDto> handleOfferConflict(
            com.backend.dealspot.exception.OfferConflictException ex, HttpServletRequest request) {
        log.warn("Handled offer conflict exception: {}", ex.getMessage());
        recordAuditError("OVERLAPPING_OFFER_REJECTED", HttpStatus.CONFLICT.value(), ex, request);
        com.backend.dealspot.dto.offer.OfferConflictResponseDto response = new com.backend.dealspot.dto.offer.OfferConflictResponseDto(
                ex.getMessage(),
                ex.getConflictingOfferId(),
                ex.getConflictingOfferTitle(),
                ex.getOfferPrice(),
                ex.getOriginalPrice(),
                ex.getValidFrom(),
                ex.getValidUntil(),
                ex.getProductId(),
                ex.getStoreId(),
                ex.getSpecialOfferId(),
                ex.getSpecialOfferTitle()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("Handled illegal argument exception: {}", ex.getMessage());
        recordAuditError("INVALID_ARGUMENT", HttpStatus.BAD_REQUEST.value(), ex, request);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Handled illegal state exception: {}", ex.getMessage());
        recordAuditError("ILLEGAL_STATE", HttpStatus.BAD_REQUEST.value(), ex, request);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Malformed JSON request payload: {}", ex.getMessage());
        recordAuditError("MALFORMED_JSON", HttpStatus.BAD_REQUEST.value(), ex, request);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Malformed or unreadable JSON request body."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("Validation failed: ");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ");
        }
        String message = sb.toString().trim();
        log.warn("Handled validation exception: {}", message);
        recordAuditError("VALIDATION_ERROR", HttpStatus.BAD_REQUEST.value(), ex, request);
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest request) {
        log.warn("Handled constraint violation: {}", ex.getMessage());
        recordAuditError("CONSTRAINT_VIOLATION", HttpStatus.BAD_REQUEST.value(), ex, request);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Validation error: " + ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex,
            HttpServletRequest request) {
        log.warn("Authentication failed: {}", ex.getMessage());
        recordAuditError("AUTH_FAILED", HttpStatus.UNAUTHORIZED.value(), ex, request);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication failed: " + ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        recordAuditError("ACCESS_DENIED", HttpStatus.FORBIDDEN.value(), ex, request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Access denied. You do not have permission to access this resource."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
            HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        recordAuditError("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("The requested resource was not found."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.error("Database integrity violation: {}", ex.getMessage(), ex);
        recordAuditError("DATA_INTEGRITY_VIOLATION", HttpStatus.CONFLICT.value(), ex, request);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "A database conflict or constraint violation occurred. Please check your data."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex,
            HttpServletRequest request) {
        log.warn("Max upload size exceeded: {}", ex.getMessage());
        recordAuditError("MAX_UPLOAD_SIZE_EXCEEDED", HttpStatus.valueOf(413).value(), ex, request);
        return ResponseEntity.status(HttpStatus.valueOf(413))
                .body(new ErrorResponse("File size exceeds maximum allowed limit."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.warn("Business runtime exception: {}", ex.getMessage());
        recordAuditError("RUNTIME_ERROR", HttpStatus.BAD_REQUEST.value(), ex, request);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled server exception: {} - {}", ex.getClass().getName(), ex.getMessage(), ex);
        recordAuditError("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected internal error occurred. Please try again later."));
    }
}