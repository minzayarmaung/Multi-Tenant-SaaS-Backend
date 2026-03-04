package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request;

import lombok.Builder;

@Builder
public record CompanyUpdateRequest(
        String name,
        String email,
        String phone,
        String address
) { }
