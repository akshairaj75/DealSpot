package com.backend.dealspot.serviceImpl.auth;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.dealspot.dto.auth.AdminRegisterRequest;
import com.backend.dealspot.dto.auth.AdminUserResponseDto;
import com.backend.dealspot.dto.auth.AuthResponseDto;
import com.backend.dealspot.dto.auth.LoginRequest;
import com.backend.dealspot.dto.auth.RegisterRequest;
import com.backend.dealspot.entity.AdminUser;
import com.backend.dealspot.entity.City;
import com.backend.dealspot.entity.User;
import com.backend.dealspot.enums.AccountType;
import com.backend.dealspot.repository.AdminUserRepository;
import com.backend.dealspot.repository.CityRepository;
import com.backend.dealspot.repository.UserRepository;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.security.JwtService;
import com.backend.dealspot.service.UserService;

@Service
public class UserServiceImpl implements UserService {

        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        private final AdminUserRepository adminUserRepository;

        private final CityRepository cityRepository;

        private final JwtService jwtService;

        public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        AdminUserRepository adminUserRepository, CityRepository cityRepository,
                        JwtService jwtService) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.adminUserRepository = adminUserRepository;
                this.cityRepository = cityRepository;
                this.jwtService = jwtService;
        }

        // @Override
        // public AuthResponseDto register(AdminRegisterDto dto) {

        // if (userRepository.existsByEmail(dto.getEmail())) {
        // throw new RuntimeException("Email already exists");
        // }

        // User user = new User();
        // user.setFullName(dto.getFullName());
        // user.setEmail(dto.getEmail());
        // user.setPhone(dto.getPhone());
        // user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        // User savedUser = userRepository.save(user);

        // CustomUserPrincipal principal = new CustomUserPrincipal(
        // savedUser.getId(),
        // savedUser.getEmail(),
        // savedUser.getPasswordHash(),
        // AccountType.USER,
        // List.of(new SimpleGrantedAuthority("ROLE_USER")),
        // true
        // );

        // String token = jwtService.generateToken(principal);

        // return AuthResponseDto.fromUser(savedUser, token);
        // }

        @Override
        public AuthResponseDto userLogin(LoginRequest request) {
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

                if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                        throw new BadCredentialsException("Invalid email or password");
                }

                CustomUserPrincipal principal = new CustomUserPrincipal(
                                user.getId(),
                                user.getEmail(),
                                user.getPasswordHash(),
                                AccountType.USER,
                                null,
                                true,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));

                String token = jwtService.generateToken(principal);

                return new AuthResponseDto(
                                user.getFullName(),
                                token,
                                user.getId(),
                                user.getEmail(),
                                "USER",
                                "USER");
        }

        @Override
        public AuthResponseDto adminLogin(LoginRequest request) {
                AdminUser admin = adminUserRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

                if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
                        throw new BadCredentialsException("Invalid email or password");
                }

                if (!admin.isActive()) {
                        throw new BadCredentialsException("Admin account is inactive");
                }

                String role = admin.getRole().name();
                Integer storeId = admin.getStore() != null ? admin.getStore().getId() : null;

                CustomUserPrincipal principal = new CustomUserPrincipal(
                                admin.getId(),
                                admin.getEmail(),
                                admin.getPasswordHash(),
                                AccountType.ADMIN,
                                admin.getRole(),
                                storeId,
                                admin.isActive(),
                                List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                String token = jwtService.generateToken(principal);

                return new AuthResponseDto(
                                admin.getFullName(),
                                token,
                                admin.getId(),
                                admin.getEmail(),
                                "ADMIN",
                                role,
                                storeId);
        }

        @Override
        public AuthResponseDto userRegister(RegisterRequest request) {
                if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("Email already exists");
                }

                User user = new User();

                user.setFullName(request.getFullName());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());

                user.setPasswordHash(
                                passwordEncoder.encode(request.getPassword()));

                if (request.getCityId() != null) {
                        City city = cityRepository.findById(request.getCityId())
                                        .orElseThrow(() -> new RuntimeException("City not found"));

                        user.setCity(city);
                }

                User savedUser = userRepository.save(user);

                CustomUserPrincipal principal = new CustomUserPrincipal(
                                savedUser.getId(),
                                savedUser.getEmail(),
                                savedUser.getPasswordHash(),
                                AccountType.USER,
                                null,
                                true,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));

                String token = jwtService.generateToken(principal);

                return new AuthResponseDto(
                                user.getFullName(),
                                token,
                                savedUser.getId(),
                                savedUser.getEmail(),
                                AccountType.USER.name(),
                                "USER");
        }

        @Override
        public AuthResponseDto createAdmin(AdminRegisterRequest request) {

                if (adminUserRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("Admin email already exists");
                }

                AdminUser admin = new AdminUser();

                admin.setFullName(request.getFullName());
                admin.setEmail(request.getEmail());
                admin.setPasswordHash(
                                passwordEncoder.encode(request.getPassword()));
                admin.setRole(request.getRole());
                admin.setActive(true);

                AdminUser savedAdmin = adminUserRepository.save(admin);

                CustomUserPrincipal principal = new CustomUserPrincipal(
                                savedAdmin.getId(),
                                savedAdmin.getEmail(),
                                savedAdmin.getPasswordHash(),
                                AccountType.ADMIN,
                                savedAdmin.getRole(),
                                savedAdmin.isActive(),
                                List.of(
                                                new SimpleGrantedAuthority(
                                                                "ROLE_" + savedAdmin.getRole().name())));

                String token = jwtService.generateToken(principal);

                return new AuthResponseDto(
                                savedAdmin.getFullName(),
                                token,
                                savedAdmin.getId(),
                                savedAdmin.getEmail(),
                                AccountType.ADMIN.name(),
                                savedAdmin.getRole().name());
        }

        @Override
        public List<AdminUserResponseDto> getAllAdmins() {
                return adminUserRepository.findAll().stream()
                                .map(AdminUserResponseDto::fromEntity)
                                .toList();
        }

        @Override
        public AdminUserResponseDto createAdminUser(AdminRegisterRequest request) {
                if (adminUserRepository.findByEmail(request.getEmail()).isPresent()) {
                        throw new RuntimeException("Admin with this email already exists");
                }

                AdminUser admin = new AdminUser();
                admin.setFullName(request.getFullName());
                admin.setEmail(request.getEmail());
                admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                admin.setRole(request.getRole() != null ? request.getRole()
                                : com.backend.dealspot.enums.AdminRole.CONTENT_MANAGER);
                admin.setActive(true);

                AdminUser saved = adminUserRepository.save(admin);
                return AdminUserResponseDto.fromEntity(saved);
        }

        @Override
        public AdminUserResponseDto toggleAdminStatus(Long id) {
                AdminUser admin = adminUserRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + id));

                admin.setActive(!admin.isActive());
                AdminUser updated = adminUserRepository.save(admin);
                return AdminUserResponseDto.fromEntity(updated);
        }

        @Override
        public void deleteAdmin(Long id) {
                if (!adminUserRepository.existsById(id)) {
                        throw new RuntimeException("Admin user not found with id: " + id);
                }
                adminUserRepository.deleteById(id);
        }

}
