package com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service.serviceImpl;

import com.project.Multi_Tenant_SaaS_Backend.common.exceptions.DuplicateEntityException;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.CompanyRepository;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.dto.request.CompanyCreateRequest;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.mapper.CompanyMapper;
import com.project.Multi_Tenant_SaaS_Backend.features.companyManagement.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

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
}
