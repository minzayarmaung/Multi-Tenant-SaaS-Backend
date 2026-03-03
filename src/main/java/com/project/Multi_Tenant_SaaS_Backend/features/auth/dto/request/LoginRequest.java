package com.project.Multi_Tenant_SaaS_Backend.features.auth.dto.request;

import jakarta.validation.Valid;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Valid
        String email,
        String password
) {}