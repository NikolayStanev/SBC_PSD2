package com.sbc.common.util;

public enum Implementation {

    MB("MB"),
    TenN("TenN"),
    Dummy("Dummy");

    public final String implementation;

    private Implementation(String implementation) {
        this.implementation = implementation;
    }

}
