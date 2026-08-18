package com.backend.dealspot.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.User;
import com.backend.dealspot.enums.AccountType;
import com.backend.dealspot.repository.AdminUserRepository;
import com.backend.dealspot.repository.UserRepository;

@Service
public class CustomUserDetailsService{


        private final UserRepository userRepository;

        private final AdminUserRepository adminUserRepository;

        public CustomUserDetailsService(UserRepository userRepository, AdminUserRepository adminUserRepository) {
            this.userRepository = userRepository;
            this.adminUserRepository = adminUserRepository;
        }

        // @Override
        // public UserDetails loadUserByUsername(String email) {

        // return adminUserRepository.findByEmail(email)
        // .map(admin -> new CustomUserPrincipal(
        // admin.getId().longValue(),
        // admin.getEmail(),
        // admin.getPasswordHash(),
        // AccountType.ADMIN,
        // List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name())),
        // admin.isActive()
        // ))
        // .orElseGet(() -> {
        // User user = userRepository.findByEmail(email)
        // .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // return new CustomUserPrincipal(
        // user.getId(),
        // user.getEmail(),
        // user.getPasswordHash(),
        // AccountType.USER,
        // List.of(new SimpleGrantedAuthority("ROLE_USER")),
        // true
        // );
        // });
        // }

        // @Override
        public CustomUserPrincipal loadUserByEmailAndAccountType(
                        String email,
                        AccountType accountType
                ) {
                if (accountType == AccountType.USER) {
                        User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                        return new CustomUserPrincipal(
                                        user.getId(),
                                        user.getEmail(),
                                        user.getPasswordHash(),
                                        AccountType.USER,
                                        null,
                                        true,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER")));
                } else if (accountType == AccountType.ADMIN) {
                        AdminUser admin = adminUserRepository.findByEmail(email)
                                        .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));

                        return new CustomUserPrincipal(
                                        admin.getId(),
                                        admin.getEmail(),
                                        admin.getPasswordHash(),
                                        AccountType.ADMIN,
                                        admin.getRole(),
                                        admin.isActive(),
                                        List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().name())));
                } else {
                        throw new IllegalArgumentException("Unknown account type");
                }
        }
}
