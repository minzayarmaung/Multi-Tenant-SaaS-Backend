package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.mapper;

import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.response.CompanyCreateResponse;

public class CompanyMapper {

    public static CompanyCreateResponse mapToResponse(Company company) {
        return CompanyCreateResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .address(company.getAddress())
                .status(company.getStatus().name())
                .build();
    }
}
