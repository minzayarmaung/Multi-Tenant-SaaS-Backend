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
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateCompanyAdminRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.CreateMemberRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.userManagement.dto.request.UpdateUserRequest;
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

        String keyword = request.getKeyword() != null ? request.getKeyword() : "";

        Page<User> page = userRepository.searchAllUsers(
                keyword, Status.ACTIVE, pageable);

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

    // ─────────────────────────────────────────────
    // COMPANY_ADMIN — List users in own company
    // ─────────────────────────────────────────────
    @Override
    public PaginatedApiResponse<UserResponse> getCompanyUsers(PaginationRequest request,
                                                              UserPrincipal principal) {
        Pageable pageable = buildPageable(request);

        String keyword = request.getKeyword() != null ? request.getKeyword() : "";

        Page<User> page = userRepository.searchUsersByCompany(
                keyword, principal.getCompanyId(), Status.ACTIVE, pageable);

        return buildPaginatedResponse(page, request);
    }

    // ─────────────────────────────────────────────
    // GET by ID
    // ─────────────────────────────────────────────
    @Override
    public ApiResponse getUserById(Long id, UserPrincipal principal) {

        User user = resolveUser(id, principal);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("User fetched successfully.")
                .data(UserMapper.mapToResponse(user))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // UPDATE BOTH COMPANY_ADMIN AND SYSTEM_USER
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse updateUser(Long id, UpdateUserRequest request, UserPrincipal principal) {

        User user = resolveUser(id, principal);

        if (request.name() != null) {
            user.setName(request.name());
        }

        // Check email uniqueness — exclude self
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new DuplicateEntityException("Email already in use: " + request.email());
            }
            user.setEmail(request.email());
        }

        // Only update password if provided
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User updated = userRepository.save(user);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("User updated successfully.")
                .data(UserMapper.mapToResponse(updated))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // SOFT DELETE COMPANY_ADMIN AND SYSTEM_USER
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse deleteUser(Long id, UserPrincipal principal) {

        User user = resolveUser(id, principal);
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("User deleted successfully.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    private User resolveUser(Long id, UserPrincipal principal) {
        if (principal.getRole() == Role.SYSTEM_ADMIN) {
            return userRepository.findById(id)
                    .filter(u -> u.getStatus() == Status.ACTIVE)
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        }
        // COMPANY_ADMIN — scoped to own company
        return userRepository.findByIdAndCompanyId(id, principal.getCompanyId())
                .filter(u -> u.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
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
