package com.sbc.psd2.data.tenN.pojo;

import java.math.BigDecimal;

public class GetTaxesPojo {

    public String accountNo;
    public BigDecimal amount;
    public String productCode;

    public GetTaxesPojo(String accountNo, BigDecimal amount) {
        this.accountNo = accountNo;
        this.amount = amount;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
