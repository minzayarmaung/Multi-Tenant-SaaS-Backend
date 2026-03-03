package com.project.Multi_Tenant_SaaS_Backend.common.response.utils;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.PaginatedApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public class ResponseUtils {

    public static ResponseEntity<ApiResponse> buildResponse(final HttpServletRequest request, final Object response) {
        ApiResponse apiResponse = (ApiResponse) response;
        final HttpStatus status = HttpStatus.valueOf(apiResponse.getCode());

        if (apiResponse.getMeta() == null) {
            final String method = request.getMethod();
            final String endpoint = request.getRequestURI();
            apiResponse.setMeta(new HashMap<>());
            apiResponse.getMeta().put("method", method);
            apiResponse.getMeta().put("endpoint", endpoint);
        }

        return new ResponseEntity<ApiResponse>( apiResponse, status);
    }
    
    public static <T> ResponseEntity<PaginatedApiResponse<T>> buildPaginatedResponse(
            final HttpServletRequest request,
            PaginatedApiResponse<T> paginatedResponse) {

        final HttpStatus status = HttpStatus.valueOf(paginatedResponse.getCode());
        if (paginatedResponse.getMeta().getMethod() == null && paginatedResponse.getMeta().getEndpoint() == null) {
            final String method = request.getMethod();
            final String endpoint = request.getRequestURI();
            paginatedResponse.getMeta().setMethod(method);
            paginatedResponse.getMeta().setEndpoint(endpoint);
        }
        return new ResponseEntity<>(paginatedResponse, status);
    }
}