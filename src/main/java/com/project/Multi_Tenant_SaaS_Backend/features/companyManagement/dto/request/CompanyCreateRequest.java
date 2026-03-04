package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request;

public record CompanyCreateRequest(
        String name,
        String email,
        String phone,
        String address
) {}