package com.sbc.psd2.data.tenN.pojo;

import java.math.BigDecimal;

public class GetTaxesPojo {

    public String customerAccount;
    public BigDecimal amount;
    public String customerNumber;


    public GetTaxesPojo(String customerAccount, BigDecimal amount, String customerNumber) {
        this.customerAccount = customerAccount;
        this.amount = amount;
        this.customerNumber = customerNumber;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Override
    public String toString() {
        return "GetTaxesPojo{" +
                "customerAccount='" + customerAccount + '\'' +
                ", amount=" + amount +
                ", customerNumber='" + customerNumber + '\'' +
                '}';
    }
}
