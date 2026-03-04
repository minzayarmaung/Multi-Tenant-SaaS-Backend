package com.project.Multi_Tenant_SaaS_Backend.features.dashboard.dto.response;

import lombok.Builder;

import java.util.Map;

// USER dashboard
@Builder
public record UserDashboardResponse(
        long totalAssignedLeads,
        long openLeads,       // NEW + CONTACTED + QUALIFIED
        long closedLeads,     // LOST + CONVERTED
        Map<String, Long> leadStatusBreakdown
) {}