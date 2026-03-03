package com.project.Multi_Tenant_SaaS_Backend.features.auth.service;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ApiResponse loginUser(LoginRequest loginRequest, HttpServletResponse httpResponse);

    ApiResponse refreshToken(HttpServletRequest request, HttpServletResponse httpResponse);

    ApiResponse logout(HttpServletRequest request, HttpServletResponse httpResponse);
}
