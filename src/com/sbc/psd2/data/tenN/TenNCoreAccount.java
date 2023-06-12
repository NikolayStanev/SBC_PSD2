package com.sbc.psd2.data.tenN;

import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;

public class TenNCoreAccount implements CoreSystemAccountInfo {

    private String accountDescription;
    private String accountOpenDate;
    private String accountStatusCreditOvd;
    private String accountStatusDebitOvd;
    private String accountClass;
    private String accountType;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String branchCode;
    private String currency;
    private String checkerDateStamp;
    private String customerAccountNumber;
    private String ibanAccountNumber;
    private int currentBalance;
    private String customerNumber;

    private String phoneNumber;



    public TenNCoreAccount() {
    }


    public String getAccountDescription() {
        return accountDescription;
    }

    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }

    public String getAccountOpenDate() {
        return accountOpenDate;
    }

    public void setAccountOpenDate(String accountOpenDate) {
        this.accountOpenDate = accountOpenDate;
    }

    public String getAccountStatusCreditOvd() {
        return accountStatusCreditOvd;
    }

    public void setAccountStatusCreditOvd(String accountStatusCreditOvd) {
        this.accountStatusCreditOvd = accountStatusCreditOvd;
    }

    public String getAccountStatusDebitOvd() {
        return accountStatusDebitOvd;
    }

    public void setAccountStatusDebitOvd(String accountStatusDebitOvd) {
        this.accountStatusDebitOvd = accountStatusDebitOvd;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public String getCheckerDateStamp() {
        return checkerDateStamp;
    }

    public void setCheckerDateStamp(String checkerDateStamp) {
        this.checkerDateStamp = checkerDateStamp;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public int getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIban() {
        return ibanAccountNumber;
    }

    @Override
    public void setIban(String iban) {
        this.ibanAccountNumber = iban;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String getProduct() {
        return accountClass;
    }

    @Override
    //TODO: check if accountClass = product
    public void setProduct(String product) {
        this.accountClass = product;
    }

    @Override
    public String getCashAccountType() {
        return accountType;
    }

    @Override
    public void setCashAccountType(String cashAccountType) {
        this.accountType = cashAccountType;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getBranch() {
        return branchCode;
    }

    @Override
    public void setBranch(String branch) {
        this.branchCode = branch;
    }

    public String getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(String accountClass) {
        this.accountClass = accountClass;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getIbanAccountNumber() {
        return ibanAccountNumber;
    }

    public void setIbanAccountNumber(String ibanAccountNumber) {
        this.ibanAccountNumber = ibanAccountNumber;
    }
}
