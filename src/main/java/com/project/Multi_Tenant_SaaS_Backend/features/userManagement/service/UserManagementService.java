package com.project.Multi_Tenant_SaaS_Backend.features.userManagement.service;

import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateCompanyAdminRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateMemberRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.UpdateUserRequest;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public interface UserManagementService {

    ApiResponse createCompanyAdmin(CreateCompanyAdminRequest request);

    PaginatedApiResponse getAllUsers(PaginationRequest request);

    ApiResponse createMember(CreateMemberRequest request, UserPrincipal principal);

    PaginatedApiResponse getCompanyUsers(PaginationRequest request, UserPrincipal principal);

    ApiResponse getUserById(Long id, UserPrincipal principal);

    ApiResponse updateUser(Long id, UpdateUserRequest request, UserPrincipal principal);

    ApiResponse deleteUser(Long id, UserPrincipal principal);
}
