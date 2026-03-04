package com.project.Multi_Tenant_SaaS_Backend.features.dashboard.service.serviceImpl;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.LeadStatus;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.CompanyRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.LeadRepository;
import com.project.Multi_Tenant_SaaS_Backend.data.repositories.UserRepository;
import com.project.Multi_Tenant_SaaS_Backend.features.dashboard.dto.response.CompanyAdminDashboardResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.dashboard.dto.response.SystemAdminDashboardResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.dashboard.dto.response.UserDashboardResponse;
import com.project.Multi_Tenant_SaaS_Backend.features.dashboard.service.DashboardService;
import com.project.Multi_Tenant_SaaS_Backend.security.JWT.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final LeadRepository leadRepository;

    @Override
    public ApiResponse getDashboard(UserPrincipal principal) {
        return switch (principal.getRole()) {
            case SYSTEM_ADMIN   -> buildSystemAdminDashboard();
            case COMPANY_ADMIN  -> buildCompanyAdminDashboard(principal);
            case USER           -> buildUserDashboard(principal);
        };
    }

    // ─────────────────────────────────────────────
    // SYSTEM_ADMIN
    // ─────────────────────────────────────────────
    private ApiResponse buildSystemAdminDashboard() {

        SystemAdminDashboardResponse data = SystemAdminDashboardResponse.builder()
                .totalCompanies(companyRepository.count())
                .activeCompanies(companyRepository.countByStatus(Status.ACTIVE))
                .totalUsers(userRepository.countByStatus(Status.ACTIVE))
                .totalLeads(leadRepository.countByStatus(Status.ACTIVE))
                .leadStatusBreakdown(aggregateLeadStatusGlobal())
                .build();

        return ApiResponse.builder()
                .success(1).code(200)
                .message("System dashboard fetched successfully.")
                .data(data)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // COMPANY_ADMIN
    // ─────────────────────────────────────────────
    private ApiResponse buildCompanyAdminDashboard(UserPrincipal principal) {

        Long companyId = principal.getCompanyId();

        CompanyAdminDashboardResponse data = CompanyAdminDashboardResponse.builder()
                .totalUsers(userRepository.countByCompanyIdAndStatus(companyId, Status.ACTIVE))
                .totalLeads(leadRepository.countByCompanyIdAndStatus(companyId, Status.ACTIVE))
                .assignedLeads(leadRepository.countByCompanyIdAndAssignedToIsNotNull(companyId))
                .unassignedLeads(leadRepository.countByCompanyIdAndAssignedToIsNull(companyId))
                .leadStatusBreakdown(aggregateLeadStatusByCompany(companyId))
                .build();

        return ApiResponse.builder()
                .success(1).code(200)
                .message("Company dashboard fetched successfully.")
                .data(data)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // USER
    // ─────────────────────────────────────────────
    private ApiResponse buildUserDashboard(UserPrincipal principal) {

        Long companyId = principal.getCompanyId();
        Long userId    = principal.getUserId();

        long totalAssigned = leadRepository
                .countByCompanyIdAndAssignedToIdAndStatus(companyId, userId, Status.ACTIVE);

        Map<String, Long> breakdown =
                aggregateLeadStatusByUser(companyId, userId);

        // Open = NEW + CONTACTED + QUALIFIED
        long openLeads = breakdown.getOrDefault("NEW", 0L)
                + breakdown.getOrDefault("CONTACTED", 0L)
                + breakdown.getOrDefault("QUALIFIED", 0L);

        // Closed = LOST + CONVERTED
        long closedLeads = breakdown.getOrDefault("LOST", 0L)
                + breakdown.getOrDefault("CONVERTED", 0L);

        UserDashboardResponse data = UserDashboardResponse.builder()
                .totalAssignedLeads(totalAssigned)
                .openLeads(openLeads)
                .closedLeads(closedLeads)
                .leadStatusBreakdown(breakdown)
                .build();

        return ApiResponse.builder()
                .success(1).code(200)
                .message("User dashboard fetched successfully.")
                .data(data)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    // ─────────────────────────────────────────────
    // Aggregation helpers — convert Object[] to Map
    // ─────────────────────────────────────────────
    private Map<String, Long> aggregateLeadStatusByCompany(Long companyId) {
        return leadRepository
                .countByLeadStatusGrouped(companyId, Status.ACTIVE)
                .stream()
                .collect(Collectors.toMap(
                        row -> ((LeadStatus) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }

    private Map<String, Long> aggregateLeadStatusByUser(Long companyId, Long userId) {
        return leadRepository
                .countByLeadStatusGroupedForUser(companyId, userId, Status.ACTIVE)
                .stream()
                .collect(Collectors.toMap(
                        row -> ((LeadStatus) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }

    private Map<String, Long> aggregateLeadStatusGlobal() {
        // Reuse existing query without company scope
        // Add this query to LeadRepository:
        // SELECT l.leadStatus, COUNT(l) FROM Lead l WHERE l.status = :status GROUP BY l.leadStatus
        return leadRepository
                .countByLeadStatusGroupedGlobal(Status.ACTIVE)
                .stream()
                .collect(Collectors.toMap(
                        row -> ((LeadStatus) row[0]).name(),
                        row -> (Long) row[1]
                ));
    }
}
