package com.backend.dealspot.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.backend.dealspot.enums.AccountType;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);
            String accountTypeValue = jwtService.extractAccountType(token);
            AccountType accountType = AccountType.valueOf(accountTypeValue);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserPrincipal principal = customUserDetailsService
                    .loadUserByEmailAndAccountType(email, accountType);

                if (jwtService.validateToken(token, principal)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities());

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication);

                    if (principal.getId() != null) {
                        MDC.put("userId", String.valueOf(principal.getId()));
                    }

                    log.debug("Authenticated user ID [{}] with role(s) [{}] for URI [{}]",
                            principal.getId(), principal.getAuthorities(), request.getRequestURI());
                } else {
                    log.warn("Invalid JWT token provided for user: {}", email);
                }
            }

        } catch (Exception exception) {
            log.warn("JWT authentication failed for URI [{} {}]: {}",
                    request.getMethod(), request.getRequestURI(), exception.getMessage());
            SecurityContextHolder.clearContext();
            MDC.put("userId", "anonymous");
        }

        filterChain.doFilter(
                request,
                response);
    }
}
