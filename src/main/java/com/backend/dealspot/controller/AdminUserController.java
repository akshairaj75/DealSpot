package com.backend.dealspot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dealspot.dto.auth.AdminRegisterRequest;
import com.backend.dealspot.dto.auth.AdminUserResponseDto;
import com.backend.dealspot.service.UserService;

@RestController
@RequestMapping("/api/dealspot/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/fetch-all")
    public ResponseEntity<List<AdminUserResponseDto>> getAllAdmins() {
        return ResponseEntity.ok(userService.getAllAdmins());
    }

    @PostMapping("/create")
    public ResponseEntity<AdminUserResponseDto> createAdminUser(@RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(userService.createAdminUser(request));
    }

    @PutMapping("/toggle/{id}")
    public ResponseEntity<AdminUserResponseDto> toggleAdminStatus(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.toggleAdminStatus(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable("id") Long id) {
        userService.deleteAdmin(id);
        return ResponseEntity.ok("Admin user deleted successfully");
    }
}
