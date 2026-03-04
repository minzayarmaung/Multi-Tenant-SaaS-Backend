package com.project.Multi_Tenant_SaaS_Backend.features.dashboard.dto.response;

import lombok.Builder;

import java.util.Map;

// SYSTEM_ADMIN dashboard
@Builder
public record SystemAdminDashboardResponse(
        long totalCompanies,
        long activeCompanies,
        long totalUsers,
        long totalLeads,
        Map<String, Long> leadStatusBreakdown  // NEW=10, CONTACTED=5, etc.
) {}