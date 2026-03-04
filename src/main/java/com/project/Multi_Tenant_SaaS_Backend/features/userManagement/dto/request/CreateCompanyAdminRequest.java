package com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request;

public record CreateCompanyAdminRequest(
        String name,
        String email,
        String password,
        Long companyId
) {}