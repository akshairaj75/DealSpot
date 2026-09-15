package com.backend.dealspot.dto.auth;

import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.User;

public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String phone;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String fullName, String email, String role, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.phone = phone;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public static UserResponseDto fromEntity(AdminUser adminUser) {
        if (adminUser == null) {
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        dto.setId(adminUser.getId());
        dto.setFullName(adminUser.getFullName());
        dto.setEmail(adminUser.getEmail());
        dto.setRole(adminUser.getRole() != null ? adminUser.getRole().name() : "ADMIN");
        return dto;
    }

    public static UserResponseDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole("USER");
        dto.setPhone(user.getPhone());
        return dto;
    }
}
