package com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service.serviceImpl;

import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.DuplicateEntityException;
import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.EntityNotFoundException;
import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.ResourceNotFoundException;
import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginationMeta;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.LeadStatus;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Role;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Lead;
import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.CompanyRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.LeadRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.request.*;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.dto.response.LeadResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.mapper.LeadMapper;
import com.project.Multi_Tenant_SaaS_Backend.features.leadManagement.service.LeadManagementService;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeadManagementServiceImpl implements LeadManagementService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    private Long getCompanyIdFromContext() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getCompanyId();
    }

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("createdAt", "updatedAt", "name", "leadStatus");

    // ─────────────────────────────────────────────
    // CREATE ROLE_COMPANY_ADMIN
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse createLead(CreateLeadRequest request, UserPrincipal principal) {

        Company company = companyRepository.findById(principal.getCompanyId())
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Company not found."));

        Lead lead = new Lead();
        lead.setName(request.name());
        lead.setEmail(request.email());
        lead.setPhone(request.phone());
        lead.setLeadStatus(LeadStatus.NEW);
        lead.setCompany(company);
        lead.setStatus(Status.ACTIVE);

        Lead saved = leadRepository.save(lead);

        return ApiResponse.builder()
                .success(1).code(201)
                .message("Lead created successfully.")
                .data(LeadMapper.mapToResponse(saved))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // LIST — COMPANY_ADMIN (all in own company)
    // ─────────────────────────────────────────────
    @Override
    public PaginatedApiResponse<LeadResponse> getCompanyLeads(PaginationRequest request,
                                                              UserPrincipal principal) {
        Pageable pageable = buildPageable(request);

        Page<Lead> page = leadRepository.searchByCompany(
                request.getKeyword(),
                principal.getCompanyId(),
                Status.ACTIVE,
                pageable
        );

        return buildPaginatedResponse(page, request);
    }

    // ─────────────────────────────────────────────
    // LIST — USER (only assigned to me)
    // ─────────────────────────────────────────────
    @Override
    public PaginatedApiResponse<LeadResponse> getMyLeads(PaginationRequest request,
                                                         UserPrincipal principal) {
        Pageable pageable = buildPageable(request);

        Page<Lead> page = leadRepository.searchAssignedToUser(
                request.getKeyword(),
                principal.getCompanyId(),
                principal.getUserId(),
                Status.ACTIVE,
                pageable
        );

        return buildPaginatedResponse(page, request);
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────
    @Override
    public ApiResponse getLeadById(Long id, UserPrincipal principal) {

        Lead lead = resolveLead(id, principal);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("Lead fetched successfully.")
                .data(LeadMapper.mapToResponse(lead))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // UPDATE full lead — COMPANY_ADMIN only
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse updateLead(Long id, UpdateLeadRequest request, UserPrincipal principal) {

        // COMPANY_ADMIN only — scoped to own company
        Lead lead = leadRepository.findByIdAndCompanyId(id, principal.getCompanyId())
                .filter(l -> l.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found: " + id));

        if (request.name() != null) {
            lead.setName(request.name());
        }

        if (request.phone() != null) {
            lead.setPhone(request.phone());
        }

        if (request.email() != null && !request.email().equals(lead.getEmail())) {
            // Check if another lead in the SAME company already has this email
            boolean emailExists = leadRepository.existsByEmailAndCompanyId(request.email(), principal.getCompanyId());
            if (emailExists) {
                throw new DuplicateEntityException("Email already in use: " + request.email());
            }
            lead.setEmail(request.email());
        }

        Lead updated = leadRepository.save(lead);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("Lead updated successfully.")
                .data(LeadMapper.mapToResponse(updated))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // PATCH status — COMPANY_ADMIN + USER
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse updateLeadStatus(Long id, UpdateLeadStatusRequest request,
                                        UserPrincipal principal) {

        Lead lead = resolveLead(id, principal);
        lead.setLeadStatus(request.status());

        Lead updated = leadRepository.save(lead);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("Lead status updated successfully.")
                .data(LeadMapper.mapToResponse(updated))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // PATCH assign — COMPANY_ADMIN only
    // ─────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse assignLead(Long id, AssignLeadRequest request, UserPrincipal principal) {

        Lead lead = leadRepository.findByIdAndCompanyId(id, principal.getCompanyId())
                .filter(l -> l.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found: " + id));

        // Critical — assignee must belong to same company
        User assignee = userRepository.findByIdAndCompanyId(request.userId(), principal.getCompanyId())
                .filter(u -> u.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found in your company: " + request.userId()));

        lead.setAssignedTo(assignee);
        Lead updated = leadRepository.save(lead);

        return ApiResponse.builder()
                .success(1).code(200)
                .message("Lead assigned successfully.")
                .data(LeadMapper.mapToResponse(updated))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────

    //  Core tenant isolation logic
    private Lead resolveLead(Long id, UserPrincipal principal) {
        if (principal.getRole() == Role.COMPANY_ADMIN) {
            // COMPANY_ADMIN — any lead in own company
            return leadRepository.findByIdAndCompanyId(id, principal.getCompanyId())
                    .filter(l -> l.getStatus() == Status.ACTIVE)
                    .orElseThrow(() -> new EntityNotFoundException("Lead not found: " + id));
        }
        // USER — only leads assigned to them
        return leadRepository.findByIdAndCompanyIdAndAssignedToId(
                        id, principal.getCompanyId(), principal.getUserId())
                .filter(l -> l.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Lead not found: " + id));
    }

    private Pageable buildPageable(PaginationRequest request) {
        String sortField = ALLOWED_SORT_FIELDS.contains(request.getSortField())
                ? request.getSortField() : "createdAt";
        Sort sort = "asc".equalsIgnoreCase(request.getSortDirection())
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }

    private PaginatedApiResponse<LeadResponse> buildPaginatedResponse(Page<Lead> page,
                                                                      PaginationRequest request) {
        return PaginatedApiResponse.<LeadResponse>builder()
                .success(1).code(200)
                .message("Leads fetched successfully.")
                .meta(PaginationMeta.builder()
                        .totalItems(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .currentPage(request.getPage())
                        .build())
                .data(page.getContent().stream().map(LeadMapper::mapToResponse).toList())
                .build();
    }
}
