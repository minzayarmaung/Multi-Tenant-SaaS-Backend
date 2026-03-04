package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.mapper;

import com.project.Multi_Tenant_SaaS_Backend.data.models.Lead;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.response.LeadResponse;

public class LeadMapper {

    public static LeadResponse mapToResponse(Lead lead) {
        return LeadResponse.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .leadStatus(lead.getLeadStatus().name())
                .companyId(lead.getCompany().getId())
                .companyName(lead.getCompany().getName())
                .assignedToId(lead.getAssignedTo() != null
                        ? lead.getAssignedTo().getId() : null)
                .assignedToEmail(lead.getAssignedTo() != null
                        ? lead.getAssignedTo().getEmail() : null)
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
}