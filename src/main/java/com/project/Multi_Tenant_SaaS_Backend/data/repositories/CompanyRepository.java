package com.project.Multi_Tenant_SaaS_Backend.data.repositories;

import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    boolean existsByName(String name);

    boolean existsByEmail(String email);
}
