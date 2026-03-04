package com.project.Multi_Tenant_SaaS_Backend.data.repositories;

import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // SYSTEM_ADMIN — all users paginated
    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.company c
    WHERE u.status = :status
    AND LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<User> searchAllUsers(@Param("keyword") String keyword,
                              @Param("status") Status status,
                              Pageable pageable);

    // COMPANY_ADMIN — own company only
    @Query("""
    SELECT u FROM User u
    WHERE u.status = :status
    AND u.company.id = :companyId
    AND LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<User> searchUsersByCompany(@Param("keyword") String keyword,
                                    @Param("companyId") Long companyId,
                                    @Param("status") Status status,
                                    Pageable pageable);

    // Scoped fetch — COMPANY_ADMIN use
    Optional<User> findByIdAndCompanyId(Long id, Long companyId);

    long countByCompanyIdAndStatus(Long companyId, Status status);
    long countByStatus(Status status);
}