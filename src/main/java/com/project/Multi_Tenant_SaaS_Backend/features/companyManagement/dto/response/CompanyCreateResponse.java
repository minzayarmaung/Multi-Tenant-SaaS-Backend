package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.response;

import lombok.Builder;

@Builder
public record CompanyCreateResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String status
) {}