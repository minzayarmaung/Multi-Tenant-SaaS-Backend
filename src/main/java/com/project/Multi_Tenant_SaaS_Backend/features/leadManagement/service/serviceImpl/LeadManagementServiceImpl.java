package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service.serviceImpl;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.LeadStatus;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Lead;
import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.CompanyRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.LeadRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request.LeadRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.mapper.LeadMapper;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service.LeadManagementService;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeadManagementServiceImpl implements LeadManagementService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    private Long getCompanyIdFromContext() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    @Override
    public ApiResponse createLead(LeadRequest request) {
        Long companyId = getCompanyIdFromContext();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Lead lead = new Lead();
        lead.setName(request.name());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setLeadStatus(request.leadStatus() != null ? LeadStatus.valueOf(request.leadStatus()) : LeadStatus.NEW);

        // Assign user only if belongs to same company
        if (request.assignedToUserId() != null) {

            User assignedUser = userRepository.findById(request.assignedToUserId())
                    .filter(u -> u.getCompany() != null &&
                            u.getCompany().getId().equals(companyId))
                    .orElseThrow(() -> new RuntimeException("User does not belong to your company"));

            lead.setAssignedTo(assignedUser);
        }

        // Set company
        lead.setCompany(company);

        Lead savedLead = leadRepository.save(lead);

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("Lead created successfully.")
                .data(LeadMapper.mapToResponse(savedLead))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }
}
