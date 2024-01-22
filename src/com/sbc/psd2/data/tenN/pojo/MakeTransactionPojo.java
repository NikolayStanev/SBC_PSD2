package com.sbc.psd2.data.tenN.pojo;

import java.math.BigDecimal;

public class MakeTransactionPojo {

    public String sourceCustomerAccount;
    public String beneficiaryCustomerAccount;
    public String beneficiaryCustomerName;
    public String beneficiaryCustomerAddress;
    public BigDecimal amount;
    public String notes;
    public String productCode;
    public String sourceCustomerNumber;



    public String getSourceCustomerAccount() {
        return sourceCustomerAccount;
    }

    public void setSourceCustomerAccount(String sourceCustomerAccount) {
        this.sourceCustomerAccount = sourceCustomerAccount;
    }

    public String getBeneficiaryCustomerAccount() {
        return beneficiaryCustomerAccount;
    }

    public void setBeneficiaryCustomerAccount(String beneficiaryCustomerAccount) {
        this.beneficiaryCustomerAccount = beneficiaryCustomerAccount;
    }

    public String getBeneficiaryCustomerName() {
        return beneficiaryCustomerName;
    }

    public void setBeneficiaryCustomerName(String beneficiaryCustomerName) {
        this.beneficiaryCustomerName = beneficiaryCustomerName;
    }

    public String getBeneficiaryCustomerAddress() {
        return beneficiaryCustomerAddress;
    }

    public void setBeneficiaryCustomerAddress(String beneficiaryCustomerAddress) {
        this.beneficiaryCustomerAddress = beneficiaryCustomerAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSourceCustomerNumber() {
        return sourceCustomerNumber;
    }

    public void setSourceCustomerNumber(String sourceCustomerNumber) {
        this.sourceCustomerNumber = sourceCustomerNumber;
    }

    @Override
    public String toString() {
        return "MakeTransactionPojo{" +
                "sourceCustomerAccount='" + sourceCustomerAccount + '\'' +
                ", beneficiaryCustomerAccount='" + beneficiaryCustomerAccount + '\'' +
                ", beneficiaryCustomerName='" + beneficiaryCustomerName + '\'' +
                ", beneficiaryCustomerAddress='" + beneficiaryCustomerAddress + '\'' +
                ", amount=" + amount +
                ", notes='" + notes + '\'' +
                ", productCode='" + productCode + '\'' +
                ", sourceCustomerNumber='" + sourceCustomerNumber + '\'' +
                '}';
    }
}
