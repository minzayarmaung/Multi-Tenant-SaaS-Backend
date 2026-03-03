package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request.LeadRequest;
import org.springframework.stereotype.Service;

@Service
public interface LeadManagementService {

    ApiResponse createLead(LeadRequest request);
}
