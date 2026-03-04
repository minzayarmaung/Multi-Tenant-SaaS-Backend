package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.controller;

import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request.*;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.response.LeadResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service.LeadManagementService;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAuthority('ROLE_COMPANY_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createLead(
            @RequestBody CreateLeadRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final ApiResponse response = this.leadService.createLead(request, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Get All Company Leads (Paginated)")
    @PreAuthorize("hasAuthority('ROLE_COMPANY_ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedApiResponse<LeadResponse>> getCompanyLeads(
            @ModelAttribute PaginationRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final PaginatedApiResponse response = this.leadService.getCompanyLeads(request, principal);
        return ResponseUtils.buildPaginatedResponse(httpRequest, response);
    }

    @Operation(summary = "Get My Leads (User Role) (Paginated)")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/my")
    public ResponseEntity<PaginatedApiResponse<LeadResponse>> getMyLeads(
            @ModelAttribute PaginationRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final PaginatedApiResponse response = this.leadService.getMyLeads(request, principal);
        return ResponseUtils.buildPaginatedResponse(httpRequest, response);
    }

    @Operation(summary = "Get Lead By Id")
    @PreAuthorize("hasAnyAuthority('ROLE_COMPANY_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLeadById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final ApiResponse response = this.leadService.getLeadById(id, principal);
        return ResponseUtils.buildResponse(httpRequest,response );
    }

    @Operation(summary = "Update Lead User By ID")
    @PreAuthorize("hasAuthority('ROLE_COMPANY_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLead(
            @PathVariable Long id,
            @RequestBody UpdateLeadRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final ApiResponse response = this.leadService.updateLead(id, request, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Update Lead Status By ID")
    @PreAuthorize("hasAnyAuthority('ROLE_COMPANY_ADMIN', 'ROLE_USER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateLeadStatus(
            @PathVariable Long id,
            @RequestBody UpdateLeadStatusRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final ApiResponse response = this.leadService.updateLeadStatus(id, request, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    private UserPrincipal getPrincipal() {
        return (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }

    @Operation(summary = "Assign Leader")
    @PreAuthorize("hasAuthority('ROLE_COMPANY_ADMIN')")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse> assignLead(
            @PathVariable Long id,
            @RequestBody AssignLeadRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final ApiResponse response = this.leadService.assignLead(id, request, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Delete Lead")
    @PreAuthorize("hasAuthority('ROLE_COMPANY_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteLead(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = getPrincipal();

        final ApiResponse response = this.leadService.deleteLead(id, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

}
