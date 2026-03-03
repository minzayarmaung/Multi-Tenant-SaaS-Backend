package com.project.Multi_Tenant_SaaS_Backend.data.repositories;

import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    boolean existsByName(String name);

    boolean existsByEmail(String email);

    @Query("""
        SELECT c FROM Company c
        WHERE c.status = :status
        AND (:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Company> searchCompanies(@Param("keyword") String keyword,
                                  @Param("status") Status status,
                                  Pageable pageable);

    Page<Company> findAllByStatus(Status status, Pageable pageable);


}
