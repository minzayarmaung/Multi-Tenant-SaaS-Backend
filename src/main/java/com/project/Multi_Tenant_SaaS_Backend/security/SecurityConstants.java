package com.project.Multi_Tenant_SaaS_Backend.security;

public class SecurityConstants {
    public static final String[] WHITELIST = {
            // Authentication & OAuth
            "/multi-tenant-SaaS-management/api/v1/auth/login",

            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private SecurityConstants(){

    }
}
