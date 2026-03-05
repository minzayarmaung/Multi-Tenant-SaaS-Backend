package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request;

public record CreateLeadRequest(
        String name,
        String email,
        String phone,
        String description
) {}
