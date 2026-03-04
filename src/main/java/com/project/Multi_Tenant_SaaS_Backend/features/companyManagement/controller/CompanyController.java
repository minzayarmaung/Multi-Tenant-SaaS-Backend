package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.controller;

import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyCreateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyUpdateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.response.CompanyResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path}/companies")
@RequiredArgsConstructor
@Tag(name = "Company Management")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "Create Company", description = "Create a new company",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Company Create request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CompanyCreateRequest.class))
            ))
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createCompany(
            @RequestBody CompanyCreateRequest request,
            HttpServletRequest httpRequest) {

        final ApiResponse response = this.companyService.createCompany(request);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Get All Companies (Paginated)")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedApiResponse<CompanyResponse>> getAllCompanies(
            @ModelAttribute PaginationRequest paginationRequest,
            HttpServletRequest httpRequest) {

        final PaginatedApiResponse response = this.companyService.getAllCompanies(paginationRequest);
        return ResponseUtils.buildPaginatedResponse(httpRequest, response);
    }

    @Operation(
            summary = "Update Company",
            description = "Update Company",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update Company Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CompanyUpdateRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Company updated successfully."),
            }
    )
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyUpdateRequest request,
            HttpServletRequest httpRequest) {

        final ApiResponse response = this.companyService.updateCompany(id , request);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Get Company By ID")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCompanyById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        final ApiResponse response = this.companyService.getCompanyById(id , httpRequest);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Delete Company (Soft Delete)")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCompany(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        final ApiResponse response = this.companyService.deleteCompany(id);
        return ResponseUtils.buildResponse(httpRequest,response);
    }
}
