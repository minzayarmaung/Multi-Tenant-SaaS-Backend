package com.project.Multi_Tenant_SaaS_Backend.data.enums;

import com.project.Multi_Tenant_SaaS_Backend.data.constant.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status implements BaseEnum<Integer> {
    ACTIVE(1),
    INACTIVE(4);

    private final int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
