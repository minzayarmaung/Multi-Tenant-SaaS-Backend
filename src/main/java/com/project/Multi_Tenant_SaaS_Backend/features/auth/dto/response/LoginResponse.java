package com.project.Multi_Tenant_SaaS_Backend.features.auth.dto.response;

import com.project.Multi_Tenant_SaaS_Backend.data.enums.Role;
import lombok.Builder;

@Builder
public record LoginResponse(
        Long userId,
        String email,
        Long companyId,
        Role role
) {}