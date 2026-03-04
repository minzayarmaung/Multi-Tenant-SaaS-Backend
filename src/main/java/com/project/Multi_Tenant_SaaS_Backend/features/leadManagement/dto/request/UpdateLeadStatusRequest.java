package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request;

import com.project.Multi_Tenant_SaaS_Backend.data.enums.LeadStatus;

public record UpdateLeadStatusRequest(
        LeadStatus status
) {}