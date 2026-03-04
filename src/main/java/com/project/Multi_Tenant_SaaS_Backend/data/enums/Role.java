package com.project.Multi_Tenant_SaaS_Backend.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    SYSTEM_ADMIN,
    COMPANY_ADMIN,
    USER
}