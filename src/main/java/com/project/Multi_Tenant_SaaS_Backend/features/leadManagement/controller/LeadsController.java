package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.controller;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request.LeadRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service.LeadManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base.path}/leads")
@RequiredArgsConstructor
@Tag(name = "Leads Management")
public class LeadsController {

    private final LeadManagementService leadService;

    @Operation(summary = "Create Lead", description = "Create a new lead for the company",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lead request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LeadRequest.class))
            ))
    @PostMapping
    public ResponseEntity<ApiResponse> createLead(@RequestBody LeadRequest request,
                                                  HttpServletRequest httpRequest) {
        final ApiResponse response = leadService.createLead(request);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

}
