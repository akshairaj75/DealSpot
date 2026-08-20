package com.backend.dealspot.dto.auth;

public class AuthResponseDto {

    private String fullName;
    private String token;
    private Long id;
    private String email;
    private String accountType;
    private String role;
    private Integer storeId;

    public AuthResponseDto(String fullName, String token, Long id, String email, String accountType, String role) {
        this(fullName, token, id, email, accountType, role, null);
    }

    public AuthResponseDto(String fullName, String token, Long id, String email, String accountType, String role, Integer storeId) {
        this.fullName = fullName;
        this.token = token;
        this.id = id;
        this.email = email;
        this.accountType = accountType;
        this.role = role;
        this.storeId = storeId;
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

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
}