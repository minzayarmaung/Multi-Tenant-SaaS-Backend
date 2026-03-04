package com.project.Multi_Tenant_SaaS_Backend.features.dashboard.dto.response;

import lombok.Builder;

import java.util.Map;

// COMPANY_ADMIN dashboard
@Builder
public record CompanyAdminDashboardResponse(
        long totalUsers,
        long totalLeads,
        long assignedLeads,
        long unassignedLeads,
        Map<String, Long> leadStatusBreakdown
) {}