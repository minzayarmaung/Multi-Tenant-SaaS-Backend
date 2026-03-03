package com.project.Multi_Tenant_SaaS_Backend.data.repositories;

import com.project.Multi_Tenant_SaaS_Backend.data.models.Company;
import com.project.Multi_Tenant_SaaS_Backend.data.models.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long>, JpaSpecificationExecutor<Lead> {
    Company findCompanyById(Long companyId);
}
