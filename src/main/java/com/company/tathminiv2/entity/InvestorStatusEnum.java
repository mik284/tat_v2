package com.company.tathminiv2.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum InvestorStatusEnum implements EnumClass<String> {

    PENDING("PENDING"),
    REJECTED("REJECTED"),
    APPROVED("APPROVED");

    private final String id;

    InvestorStatusEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static InvestorStatusEnum fromId(String id) {
        for (InvestorStatusEnum at : InvestorStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}