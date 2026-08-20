package com.backend.dealspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import com.backend.dealspot.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // @Bean
    // public PasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    // }

    // @Bean
    // public SecurityFilterChain securityFilterChain(
    // HttpSecurity http) throws Exception {
    // http
    // .csrf(csrf -> csrf.disable())
    // .sessionManagement(session -> session
    // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
    // .authorizeHttpRequests(auth -> auth
    // // .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
    // // .anyRequest().authenticated()
    // .anyRequest().permitAll())
    // .formLogin(Customizer.withDefaults());

    // return http.build();
    // }



    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }   

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.IF_REQUIRED))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/api/dealspot/auth/**",
                                "/api/dealspot/partner-requests/apply",
                                "/api/oauth2/**",
                                "/uploads/**",
                                "/login/oauth2/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/ws/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, 
                                "/api/dealspot/stores/**",
                                "/api/dealspot/store-branches/**",
                                "/api/dealspot/categories/**",
                                "/api/dealspot/offers/**",
                                "/api/dealspot/flyers/**",
                                "/api/dealspot/cities/**",
                                "/api/dealspot/products/**",
                                "/api/dealspot/coupons/**",
                                "/api/dealspot/brands/**"
                        ).permitAll()
                        .requestMatchers("/api/chat/**")
                        .authenticated()
                        .anyRequest().authenticated())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                    {
                                            "message": "Unauthorized"
                                    }
                                    """);
                        }))

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));
        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
