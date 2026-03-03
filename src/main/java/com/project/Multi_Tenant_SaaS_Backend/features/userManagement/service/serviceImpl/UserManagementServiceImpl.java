package com.project.Multi_Tenant_SaaS_Backend.features.userManagement.service.serviceImpl;

import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.DuplicateEntityException;
import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.EntityNotFoundException;
import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginationMeta;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Role;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.CompanyRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import com.project.Multi_Tenant_SaaS_Backend.features.auth.mapper.AuthMapper;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateCompanyAdminRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateMemberRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.response.UserResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.mapper.UserMapper;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.service.UserManagementService;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static com.project.Multi_Tenant_SaaS_Backend.common.response.utils.ResponseUtils.buildPaginatedResponse;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "name", "email"
    );

    // ─────────────────────────────────────────────
    // SYSTEM_ADMIN — Create COMPANY_ADMIN
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse createCompanyAdmin(CreateCompanyAdminRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEntityException("Email already in use: " + request.email());
        }

        Company company = companyRepository.findById(request.companyId())
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company not found: " + request.companyId()));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.COMPANY_ADMIN);
        user.setCompany(company);
        user.setStatus(Status.ACTIVE);

        User saved = userRepository.save(user);

        return ApiResponse.builder()
                .success(1).code(201)
                .message("Company admin created successfully.")
                .data(UserMapper.mapToResponse(saved))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // SYSTEM_ADMIN — List all users
    // ─────────────────────────────────────────────
    @Override
    public PaginatedApiResponse<UserResponse> getAllUsers(PaginationRequest request) {

        Pageable pageable = buildPageable(request);

        Page<User> page = userRepository.searchAllUsers(
                request.getKeyword(), Status.ACTIVE, pageable);

        return buildPaginatedResponse(page, request);
    }

    // ─────────────────────────────────────────────
    // COMPANY_ADMIN — Create USER in own company
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse createMember(CreateMemberRequest request, UserPrincipal principal) {

        // Tenant guard — companyId comes from the token, NOT the request body
        Company company = companyRepository.findById(principal.getCompanyId())
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Company not found."));

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEntityException("Email already in use: " + request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setCompany(company);
        user.setStatus(Status.ACTIVE);

        User saved = userRepository.save(user);

        return ApiResponse.builder()
                .success(1).code(201)
                .message("Member created successfully.")
                .data(UserMapper.mapToResponse(saved))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    private Pageable buildPageable(PaginationRequest request) {
        String sortField = ALLOWED_SORT_FIELDS.contains(request.getSortField())
                ? request.getSortField() : "createdAt";
        Sort sort = "asc".equalsIgnoreCase(request.getSortDirection())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private PaginatedApiResponse<UserResponse> buildPaginatedResponse(Page<User> page,
                                                                      PaginationRequest request) {
        return PaginatedApiResponse.<UserResponse>builder()
                .success(1).code(200)
                .message("Users fetched successfully.")
                .meta(PaginationMeta.builder()
                        .totalItems(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .currentPage(request.getPage())
                        .build())
                .data(page.getContent().stream().map(UserMapper::mapToResponse).toList())
                .build();
    }
}
