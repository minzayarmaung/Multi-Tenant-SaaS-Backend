package com.project.Multi_Tenant_SaaS_Backend.common.exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}