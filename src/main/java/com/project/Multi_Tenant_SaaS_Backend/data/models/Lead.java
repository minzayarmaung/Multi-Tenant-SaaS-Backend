package com.project.Multi_Tenant_SaaS_Backend.data.models;

import com.project.Multi_Tenant_SaaS_Backend.data.common.Auditable;
import com.project.Multi_Tenant_SaaS_Backend.data.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "leads",
        indexes = {
                @Index(name = "idx_leads_company", columnList = "company_id"),
                @Index(name = "idx_leads_status", columnList = "status"),
                @Index(name = "idx_leads_assigned", columnList = "assigned_to")
        }
)
@Getter
@Setter
public class Lead extends Auditable {

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus leadStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
