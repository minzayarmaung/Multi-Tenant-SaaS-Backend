package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service;

import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyCreateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public interface CompanyService {
    ApiResponse createCompany(CompanyCreateRequest request);

    PaginatedApiResponse getAllCompanies(PaginationRequest paginationRequest);

    ApiResponse updateCompany(Long id, CompanyUpdateRequest request);

    ApiResponse getCompanyById(Long id, HttpServletRequest httpRequest);

    ApiResponse deleteCompany(Long id);
}
