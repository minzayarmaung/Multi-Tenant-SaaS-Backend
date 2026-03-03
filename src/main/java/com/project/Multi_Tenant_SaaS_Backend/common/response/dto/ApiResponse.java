package com.project.Multi_Tenant_SaaS_Backend.common.response.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class ApiResponse { // POJO
    private int success;
    private int code;
    private Map<String, Object> meta;
    private Object data;
    private String message;
}