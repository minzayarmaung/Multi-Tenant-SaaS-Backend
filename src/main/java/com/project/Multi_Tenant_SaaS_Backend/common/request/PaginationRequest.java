package com.project.Multi_Tenant_SaaS_Backend.common.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationRequest {
    private String keyword;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sortField = "createdAt";

    @Builder.Default
    private String sortDirection = "desc";
}