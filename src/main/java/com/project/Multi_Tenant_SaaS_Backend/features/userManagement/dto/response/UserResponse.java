package com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        String status,
        Long companyId,
        String companyName,
        LocalDateTime createdAt
) {}