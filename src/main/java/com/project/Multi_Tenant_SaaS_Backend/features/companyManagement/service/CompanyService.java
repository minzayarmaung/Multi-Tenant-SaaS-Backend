package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyCreateRequest;
import org.springframework.stereotype.Service;

@Service
public interface CompanyService {
    ApiResponse createCompany(CompanyCreateRequest request);
}
