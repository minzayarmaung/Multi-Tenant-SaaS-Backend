package com.project.Multi_Tenant_SaaS_Backend.data.models;

import com.project.Multi_Tenant_SaaS_Backend.data.common.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company extends Auditable {

    @Column(nullable = false, unique = true)
    private String name;

    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = false)
    private String address;
}
