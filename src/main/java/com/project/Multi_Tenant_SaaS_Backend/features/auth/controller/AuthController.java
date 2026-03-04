package com.project.Multi_Tenant_SaaS_Backend.features.auth.controller;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.dto.request.LoginRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth")
@Tag(name = "Auth API", description = "Endpoints for managing Auth Apis")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login User",
            description = "Login User and return JWT in secure cookie",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest,
                                             HttpServletRequest request,
                                             HttpServletResponse httpResponse) {
        final ApiResponse response = authService.loginUser(loginRequest, httpResponse);
        return ResponseUtils.buildResponse(request, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request,
                                                    HttpServletResponse httpResponse) {
        final ApiResponse response = authService.refreshToken(request, httpResponse);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request,
                                              HttpServletResponse httpResponse) {
        final ApiResponse response = authService.logout(request, httpResponse);
        return ResponseUtils.buildResponse(request, response);
    }

}
