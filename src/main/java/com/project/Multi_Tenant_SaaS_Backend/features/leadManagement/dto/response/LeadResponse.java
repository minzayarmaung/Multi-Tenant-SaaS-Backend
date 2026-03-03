package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.response;

import lombok.Builder;

@Builder
public record LeadResponse(
        Long id,
        String name,
        String email,
        String phone,
        String status,
        Long assignedToUserId
) {}