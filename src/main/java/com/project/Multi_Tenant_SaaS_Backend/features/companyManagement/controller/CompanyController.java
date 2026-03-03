package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.controller;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyCreateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        final ApiResponse response = companyService.createCompany(request);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

}
