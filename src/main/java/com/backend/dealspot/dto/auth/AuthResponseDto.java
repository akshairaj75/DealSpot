package com.backend.dealspot.dto.auth;

public class AuthResponseDto {

    private String fullName;
    private String token;
    private Long id;
    private String email;
    private String accountType;
    private String role;

    public AuthResponseDto(String fullName, String token, Long id, String email, String accountType, String role) {
        this.fullName = fullName;
        this.token = token;
        this.id = id;
        this.email = email;
        this.accountType = accountType;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getRole() {
        return role;
    }
}