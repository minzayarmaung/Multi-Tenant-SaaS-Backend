package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CompanyResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String status,
        LocalDateTime createdAt
) {
}
