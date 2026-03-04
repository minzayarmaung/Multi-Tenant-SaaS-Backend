package com.project.Multi_Tenant_SaaS_Backend.data.converter;

import com.project.Multi_Tenant_SaaS_Backend.data.enums.Status;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter extends BaseEnumConverter<Status, Integer>{

    public StatusConverter() {
        super(Status.class);
    }
}
