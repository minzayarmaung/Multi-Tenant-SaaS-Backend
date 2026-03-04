package com.project.Multi_Tenant_SaaS_Backend.features.auth.mapper;

import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.dto.response.LoginResponse;

public class AuthMapper {

    public static LoginResponse mapUserToLogInResponse(User user) {

        Long companyId = user.getCompany() != null
                ? user.getCompany().getId()
                : null;

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .companyId(companyId)
                .role(user.getRole())
                .build();
    }
}