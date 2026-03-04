package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request;

public record UpdateLeadRequest(
        String name,
        String email,
        String phone
) {}