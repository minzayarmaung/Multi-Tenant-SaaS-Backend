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
                .status(lead.getStatus() != null ? lead.getStatus().name() : null)
                .assignedToUserId(lead.getAssignedTo() != null ? lead.getAssignedTo().getId() : null)
                .build();
    }
}