package com.project.Multi_Tenant_SaaS_Backend.data.repositories;

import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    // COMPANY_ADMIN — all leads in own company
    @Query("""
    SELECT l FROM Lead l
    LEFT JOIN FETCH l.assignedTo
    WHERE l.company.id = :companyId
    AND l.status = :status
    AND LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Lead> searchByCompany(@Param("keyword") String keyword,
                               @Param("companyId") Long companyId,
                               @Param("status") Status status,
                               Pageable pageable);

    // USER — only leads assigned to me
    @Query("""
        SELECT l FROM Lead l
        WHERE l.company.id = :companyId
        AND l.assignedTo.id = :userId
        AND l.status = :status
        AND (:keyword IS NULL
             OR LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Lead> searchAssignedToUser(@Param("keyword") String keyword,
                                    @Param("companyId") Long companyId,
                                    @Param("userId") Long userId,
                                    @Param("status") Status status,
                                    Pageable pageable);

    // Scoped fetch — tenant safe
    Optional<Lead> findByIdAndCompanyId(Long id, Long companyId);

    // USER scoped fetch — assigned + company
    Optional<Lead> findByIdAndCompanyIdAndAssignedToId(Long id, Long companyId, Long assignedToId);

    // Dashboard queries — single aggregation
    long countByCompanyIdAndStatus(Long companyId, Status status);
    long countByCompanyIdAndAssignedToIsNullAndStatus(Long companyId , Status status);
    long countByCompanyIdAndAssignedToIsNotNull(Long companyId);

    @Query("""
        SELECT l.leadStatus, COUNT(l) FROM Lead l
        WHERE l.company.id = :companyId
        AND l.status = :status
        GROUP BY l.leadStatus
        """)
    List<Object[]> countByLeadStatusGrouped(@Param("companyId") Long companyId,
                                            @Param("status") Status status);

    boolean existsByEmailAndCompanyId(String email, Long companyId);

    // USER dashboard — leads assigned to me
    long countByCompanyIdAndAssignedToIdAndStatus(
            Long companyId, Long userId, Status status);

    @Query("""
    SELECT l.leadStatus, COUNT(l) FROM Lead l
    WHERE l.company.id = :companyId
    AND l.assignedTo.id = :userId
    AND l.status = :status
    GROUP BY l.leadStatus
    """)
    List<Object[]> countByLeadStatusGroupedForUser(
            @Param("companyId") Long companyId,
            @Param("userId") Long userId,
            @Param("status") Status status);

    @Query("""
    SELECT l.leadStatus, COUNT(l) FROM Lead l
    WHERE l.status = :status
    GROUP BY l.leadStatus
    """)
    List<Object[]> countByLeadStatusGroupedGlobal(@Param("status") Status status);

    long countByStatus(Status status); // SYSTEM_ADMIN total leads
}