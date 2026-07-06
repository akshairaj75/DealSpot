package com.backend.dealspot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.auth.AdminRegisterDto;

@RestController
@RequestMapping("/api/dealspot/auth")
public class AuthController {

    @PostMapping("/register-admin")
    public ResponseEntity<AdminRegisterDto> registerAdmin(
        @RequestBody AdminRegisterDto dto) {

        AdminRegisterDto res = authService.registerAdmin(dto);
        return ResponseEntity.ok(res);
    }
    
}
