package com.project.Multi_Tenant_SaaS_Backend.common.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
