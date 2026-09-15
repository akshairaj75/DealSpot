package com.backend.dealspot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.auth.AdminRegisterRequest;
import com.backend.dealspot.dto.auth.AuthResponseDto;
import com.backend.dealspot.dto.auth.LoginRequest;
import com.backend.dealspot.dto.auth.RegisterRequest;
import com.backend.dealspot.enums.AuditAction;
import com.backend.dealspot.service.AuditLogService;
import com.backend.dealspot.service.UserService;

@RestController
@RequestMapping("/api/dealspot/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuditLogService auditLogService;

    public AuthController(UserService userService, AuditLogService auditLogService) {
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Received user registration request for email: {}", request.getEmail());
        try {
            AuthResponseDto response = userService.userRegister(request);
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.REGISTER_USER,
                    response.getId(),
                    null,
                    "POST",
                    "/api/dealspot/auth/user/register",
                    200,
                    true,
                    null,
                    null,
                    duration,
                    null,
                    null
            );
            log.info("User registered successfully with email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.REGISTER_USER,
                    null,
                    null,
                    "POST",
                    "/api/dealspot/auth/user/register",
                    400,
                    false,
                    null,
                    null,
                    duration,
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @PostMapping("/admin/create")
    public ResponseEntity<AuthResponseDto> createAdmin(@RequestBody AdminRegisterRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Received admin creation request for email: {} with role: {}", request.getEmail(), request.getRole());
        try {
            AuthResponseDto response = userService.createAdmin(request);
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.CREATE_ADMIN,
                    response.getId(),
                    null,
                    "POST",
                    "/api/dealspot/auth/admin/create",
                    200,
                    true,
                    null,
                    null,
                    duration,
                    null,
                    null
            );
            log.info("Admin created successfully for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.CREATE_ADMIN,
                    null,
                    null,
                    "POST",
                    "/api/dealspot/auth/admin/create",
                    400,
                    false,
                    null,
                    null,
                    duration,
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<AuthResponseDto> userLogin(@RequestBody LoginRequest request) {
        long startTime = System.currentTimeMillis();
        log.debug("User login attempt for email: {}", request.getEmail());
        try {
            AuthResponseDto response = userService.userLogin(request);
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.LOGIN,
                    response.getId(),
                    null,
                    "POST",
                    "/api/dealspot/auth/user/login",
                    200,
                    true,
                    null,
                    null,
                    duration,
                    null,
                    null
            );
            log.info("User login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.LOGIN,
                    null,
                    null,
                    "POST",
                    "/api/dealspot/auth/user/login",
                    401,
                    false,
                    null,
                    null,
                    duration,
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
            throw ex;
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponseDto> adminLogin(@RequestBody LoginRequest request) {
        long startTime = System.currentTimeMillis();
        log.debug("Admin login attempt for email: {}", request.getEmail());
        try {
            AuthResponseDto response = userService.adminLogin(request);
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.LOGIN,
                    response.getId(),
                    null,
                    "POST",
                    "/api/dealspot/auth/admin/login",
                    200,
                    true,
                    null,
                    null,
                    duration,
                    null,
                    null
            );
            log.info("Admin login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            auditLogService.log(
                    AuditAction.LOGIN,
                    null,
                    null,
                    "POST",
                    "/api/dealspot/auth/admin/login",
                    401,
                    false,
                    null,
                    null,
                    duration,
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
            throw ex;
        }
    }
}
