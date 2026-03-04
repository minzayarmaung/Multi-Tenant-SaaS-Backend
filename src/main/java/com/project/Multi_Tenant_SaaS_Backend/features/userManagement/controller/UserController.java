package com.project.Multi_Tenant_SaaS_Backend.features.userManagement.controller;

import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyCreateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateCompanyAdminRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateMemberRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.UpdateUserRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.response.UserResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.service.UserManagementService;
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
@RequestMapping("${api.base.path}/users")
@RequiredArgsConstructor
@Tag(name = "User Management" , description = "Endpoints for managing users")
public class UserController {

    private final UserManagementService userService;

    @Operation(summary = "Create Company Admin", description = "Create a new Company Admin",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Company Admin Create request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCompanyAdminRequest.class))
            ))
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createCompanyAdmin(
            @RequestBody CreateCompanyAdminRequest request,
            HttpServletRequest httpRequest) {

        final ApiResponse response = this.userService.createCompanyAdmin(request);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Create Company Members")
    @PreAuthorize("hasAnyAuthority('ROLE_COMPANY_ADMIN'")
    @PostMapping("/members")
    public ResponseEntity<ApiResponse> createMember(
            @RequestBody CreateMemberRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final ApiResponse response = this.userService.createMember(request, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Get All Company Admins (Paginated)")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedApiResponse<UserResponse>> getAllUsers(
            @ModelAttribute PaginationRequest request,
            HttpServletRequest httpRequest) {

        final PaginatedApiResponse response = this.userService.getAllUsers(request);
        return ResponseUtils.buildPaginatedResponse(httpRequest, response);
    }

    @Operation(summary = "Get All Company Users (Paginated)")
    @PreAuthorize("hasAuthority('ROLE_COMPANY_ADMIN')")
    @GetMapping("/company")
    public ResponseEntity<PaginatedApiResponse<UserResponse>> getCompanyUsers(
            @ModelAttribute PaginationRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        final PaginatedApiResponse response = this.userService.getCompanyUsers(request, principal);
        return ResponseUtils.buildPaginatedResponse(httpRequest, response);
    }

    @Operation(summary = "Get User By Id")
    @PreAuthorize("hasAnyAuthority('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        final ApiResponse response = this.userService.getUserById(id, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Update User")
    @PreAuthorize("hasAnyAuthority('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        final ApiResponse response = this.userService.updateUser(id, request, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(summary = "Delete User by Id")
    @PreAuthorize("hasAnyAuthority('ROLE_SYSTEM_ADMIN', 'ROLE_COMPANY_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        final ApiResponse response = this.userService.deleteUser(id, principal);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

}
