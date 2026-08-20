package com.backend.dealspot.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.backend.dealspot.enums.AccountType;
import com.backend.dealspot.enums.AdminRole;

public class CustomUserPrincipal implements UserDetails {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private AccountType accountType;
    private AdminRole role;
    private Integer storeId;

    private Collection<? extends GrantedAuthority> authorities;
    private boolean active;

    public CustomUserPrincipal(
            Long id,
            String email,
            String password,
            AccountType accountType,
            AdminRole role,
            boolean active,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this(id, email, password, accountType, role, null, active, authorities);
    }

    public CustomUserPrincipal(
            Long id,
            String email,
            String password,
            AccountType accountType,
            AdminRole role,
            Integer storeId,
            boolean active,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
        this.role = role;
        this.storeId = storeId;
        this.active = active;
        this.authorities = authorities;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public boolean canManageStore(Integer targetStoreId) {
        if (role == AdminRole.SUPER_ADMIN) {
            return true;
        }
        return storeId != null && storeId.equals(targetStoreId);
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}