package com.project.Multi_Tenant_SaaS_Backend.features.dashboard.service;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public interface DashboardService {
    ApiResponse getDashboard(UserPrincipal principal);
}
