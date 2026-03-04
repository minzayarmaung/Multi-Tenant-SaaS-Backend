package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service;

import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request.*;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public interface LeadManagementService {

    ApiResponse createLead(CreateLeadRequest request, UserPrincipal principal);

    PaginatedApiResponse getCompanyLeads(PaginationRequest request, UserPrincipal principal);

    PaginatedApiResponse getMyLeads(PaginationRequest request, UserPrincipal principal);

    ApiResponse getLeadById(Long id, UserPrincipal principal);

    ApiResponse updateLead(Long id, UpdateLeadRequest request, UserPrincipal principal);

    ApiResponse updateLeadStatus(Long id, UpdateLeadStatusRequest request, UserPrincipal principal);

    ApiResponse assignLead(Long id, AssignLeadRequest request, UserPrincipal principal);
}
