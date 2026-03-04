package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request;

public record AssignLeadRequest(
        Long userId   // must belong to same company
) {}

