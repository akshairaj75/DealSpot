package com.backend.dealspot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.auth.AdminRegisterRequest;
import com.backend.dealspot.dto.auth.AuthResponseDto;
import com.backend.dealspot.dto.auth.LoginRequest;
import com.backend.dealspot.dto.auth.RegisterRequest;
import com.backend.dealspot.service.UserService;

@RestController
@RequestMapping("/api/dealspot/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<AuthResponseDto> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                userService.userRegister(request));
    }

    @PostMapping("/admin/create")
    public ResponseEntity<AuthResponseDto> createAdmin(
            @RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(userService.createAdmin(request));
    }

    @PostMapping("/user/login")
    public ResponseEntity<AuthResponseDto> userLogin(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.userLogin(request));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponseDto> adminLogin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.adminLogin(request));
    }

}
