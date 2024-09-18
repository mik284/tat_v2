package com.company.tathminiv2.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum RiskProfileEnum implements EnumClass<String> {

    AGGRESSIVE("AGGRESSIVE"),
    MODERATE("MODERATE"),
    CONSERVATIVE("CONSERVATIVE");

    private final String id;

    RiskProfileEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RiskProfileEnum fromId(String id) {
        for (RiskProfileEnum at : RiskProfileEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}