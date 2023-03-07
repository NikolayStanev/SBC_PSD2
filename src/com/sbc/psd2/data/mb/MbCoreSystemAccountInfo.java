package com.sbc.psd2.data.mb;

import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;

public class MbCoreSystemAccountInfo implements CoreSystemAccountInfo {

    private String iban;
    private String currency;
    private String product;
    private String cashAccountType;
    private String name;

    private String branch;

    public MbCoreSystemAccountInfo() {
    }

    public MbCoreSystemAccountInfo(String iban, String currency, String product, String cashAccountType, String name) {
        this.iban = iban;
        this.currency = currency;
        this.product = product;
        this.cashAccountType = cashAccountType;
        this.name = name;
    }

    @Override
    public String getIban() {
        return this.iban;
    }

    @Override
    public void setIban(String iban) {
        this.iban = iban;
    }

    @Override
    public String getCurrency() {
        return this.currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String getProduct() {
        return this.product;
    }

    @Override
    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String getCashAccountType() {
        return this.cashAccountType;
    }

    @Override
    public void setCashAccountType(String cashAccountType) {
        this.cashAccountType = cashAccountType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getBranch() {
        return this.branch;
    }

    @Override
    public void setBranch(String branch) {
        this.branch=branch;
    }
}
