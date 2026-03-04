package com.project.Multi_Tenant_SaaS_Backend.features.dashboard.controller;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils;
import com.project.Multi_Tenant_SaaS_Backend.features.dashboard.service.DashboardService;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/dashboard")
@Tag(name = "Dashboard API", description = "Aggregated stats per role")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get Dashboard",
            description = "Returns role-specific dashboard data")
    @PreAuthorize("hasAnyAuthority('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<ApiResponse> getDashboard(HttpServletRequest httpRequest) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final ApiResponse response = this.dashboardService.getDashboard(principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }
}