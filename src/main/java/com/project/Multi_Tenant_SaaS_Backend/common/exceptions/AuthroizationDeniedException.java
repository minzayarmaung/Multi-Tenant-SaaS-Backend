package com.project.Multi_Tenant_SaaS_Backend.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthroizationDeniedException extends RuntimeException {
    public AuthroizationDeniedException(String message) {
        super(message);
    }
}
