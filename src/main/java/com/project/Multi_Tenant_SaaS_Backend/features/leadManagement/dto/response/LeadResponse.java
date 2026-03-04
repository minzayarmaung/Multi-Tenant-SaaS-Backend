package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LeadResponse(
        Long id,
        String name,
        String email,
        String phone,
        String leadStatus,
        Long companyId,
        String companyName,
        Long assignedToId,
        String assignedToEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}