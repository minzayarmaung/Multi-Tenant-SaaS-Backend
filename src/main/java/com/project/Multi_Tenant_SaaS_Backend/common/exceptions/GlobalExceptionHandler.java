package com.project.Multi_Tenant_SaaS_Backend.common.exceptions;

import com.project.Multi_Tenant_SaaS_Backend.common.response.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ApiResponse response = ApiResponse.builder()
                .success(0)
                .code(400)
                .message(errorMessage)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEntity(
            DuplicateEntityException ex
    ) {
        ApiResponse response = ApiResponse.builder()
                .success(0)
                .code(409)
                .message(ex.getMessage())
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AuthroizationDeniedException.class)
    public ResponseEntity<ApiResponse> handleAuthroizationDenicedException(UnauthorizedException ex) {
        ApiResponse response = ApiResponse.builder()
                .success(0)
                .code(403 )
                .message(ex.getMessage())
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ApiResponse response = ApiResponse.builder()
                .success(0)
                .code(403)
                .message("Permission Denied.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception ex) {

        ApiResponse response = ApiResponse.builder()
                .success(0)
                .code(500)
                .message("Something went wrong.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotException(Exception ex) {

        ApiResponse response = ApiResponse.builder()
                .success(0)
                .code(404)
                .message("Entity Not Found.")
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
}

