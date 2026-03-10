package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service.serviceImpl;

import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.DuplicateEntityException;
import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.EntityNotFoundException;
import com.project.Multi_Tenant_SaaS_Backend.common.request.PaginationRequest;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginationMeta;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.CompanyRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyCreateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyUpdateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.response.CompanyResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.mapper.CompanyMapper;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service.CompanyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse createCompany(CompanyCreateRequest request) {

        if (companyRepository.existsByName(request.name())) {
            throw new DuplicateEntityException("Company with name '" + request.name() + "' already exists.");
        }

        if (companyRepository.existsByEmail(request.email())) {
            throw new DuplicateEntityException("Company with email '" + request.email() + "' already exists.");
        }

        Company company = new Company();
        company.setName(request.name());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        company.setAddress(request.address());
        company.setStatus(Status.ACTIVE);

        Company saved = companyRepository.save(company);

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("Company created successfully.")
                .data(CompanyMapper.mapToResponse(saved))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public PaginatedApiResponse getAllCompanies(PaginationRequest paginationRequest) {

        Sort sort = paginationRequest.getSortDirection().equalsIgnoreCase("desc")
                ? Sort.by(paginationRequest.getSortField()).descending()
                : Sort.by(paginationRequest.getSortField()).ascending();

        Pageable pageable = PageRequest.of(
                paginationRequest.getPage(),
                paginationRequest.getSize(),
                sort
        );

        String keyword = paginationRequest.getKeyword() != null ? paginationRequest.getKeyword() : "";

        Page<Company> page = (keyword != null && !keyword.isBlank())
                ? companyRepository.searchCompanies(keyword, Status.ACTIVE, pageable)
                : companyRepository.findAllByStatus(Status.ACTIVE, pageable);

        List<CompanyResponse> data = page.getContent()
                .stream()
                .map(CompanyMapper::mapToResponseList)
                .toList();

        return PaginatedApiResponse.<CompanyResponse>builder()
                .success(1)
                .code(200)
                .message("Companies fetched successfully.")
                .meta(PaginationMeta.builder()
                        .totalItems(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .currentPage(paginationRequest.getPage())
                        .build())
                .data(data)
                .build();
    }

    @Override
    public ApiResponse updateCompany(Long id, CompanyUpdateRequest request) {

        Company company = companyRepository.findById(id)
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        // Check name uniqueness — exclude self
        if (request.name() != null && !request.name().equals(company.getName())) {
            if (companyRepository.existsByName(request.name())) {
                throw new DuplicateEntityException("Company name '" + request.name() + "' is already taken.");
            }
            company.setName(request.name());
        }

        // Check email uniqueness — exclude self
        if (request.email() != null && !request.email().equals(company.getEmail())) {
            if (companyRepository.existsByEmail(request.email())) {
                throw new DuplicateEntityException("Company email '" + request.email() + "' is already taken.");
            }
            company.setEmail(request.email());
        }

        if (request.phone() != null) {
            company.setPhone(request.phone());
        }

        if (request.address() != null) {
            company.setAddress(request.address());
        }

        Company updated = companyRepository.save(company);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Company updated successfully.")
                .data(CompanyMapper.mapToResponse(updated))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse getCompanyById(Long id, HttpServletRequest httpRequest) {

        Company company = companyRepository.findById(id)
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Company fetched successfully.")
                .data(CompanyMapper.mapToResponse(company))
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteCompany(Long id) {

        Company company = companyRepository.findById(id)
                .filter(c -> c.getStatus() == Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        userRepository.deactivateUsersByCompanyId(id);

        company.setStatus(Status.INACTIVE);
        companyRepository.save(company);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Company deleted successfully.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }
}
