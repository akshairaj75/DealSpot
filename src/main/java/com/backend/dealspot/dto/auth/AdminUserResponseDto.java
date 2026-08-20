package com.backend.dealspot.dto.auth;

import java.time.LocalDateTime;

import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.enums.AdminRole;

public class AdminUserResponseDto {

    private Long id;
    private String fullName;
    private String email;
    private AdminRole role;
    private boolean active;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    public AdminUserResponseDto() {
    }

    public AdminUserResponseDto(Long id, String fullName, String email, AdminRole role, boolean active,
            LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }

    public static AdminUserResponseDto fromEntity(AdminUser admin) {
        return new AdminUserResponseDto(
                admin.getId(),
                admin.getFullName(),
                admin.getEmail(),
                admin.getRole(),
                admin.isActive(),
                admin.getLastLoginAt(),
                admin.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
