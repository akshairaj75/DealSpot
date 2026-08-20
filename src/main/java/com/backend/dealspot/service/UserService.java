package com.backend.dealspot.service;

import java.util.List;

import com.backend.dealspot.dto.auth.AdminRegisterRequest;
import com.backend.dealspot.dto.auth.AdminUserResponseDto;
import com.backend.dealspot.dto.auth.AuthResponseDto;
import com.backend.dealspot.dto.auth.LoginRequest;
import com.backend.dealspot.dto.auth.RegisterRequest;

public interface UserService {

    AuthResponseDto adminLogin(LoginRequest request);

    AuthResponseDto userLogin(LoginRequest request);

    AuthResponseDto userRegister(RegisterRequest request);

    AuthResponseDto createAdmin(AdminRegisterRequest request);

    List<AdminUserResponseDto> getAllAdmins();

    AdminUserResponseDto createAdminUser(AdminRegisterRequest request);

    AdminUserResponseDto toggleAdminStatus(Long id);

    void deleteAdmin(Long id);

}

