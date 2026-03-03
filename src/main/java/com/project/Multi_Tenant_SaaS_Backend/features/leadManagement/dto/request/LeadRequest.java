package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request;

import lombok.Builder;

@Builder
public record LeadRequest(
        String name,
        String email,
        String phone,
        String leadStatus,
        Long assignedToUserId
) {}