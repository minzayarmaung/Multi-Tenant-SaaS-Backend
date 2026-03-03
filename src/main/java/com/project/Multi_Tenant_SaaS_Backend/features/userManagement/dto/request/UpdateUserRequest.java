package com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request;

public record UpdateUserRequest(
        String email,
        String password
) {}