package com.sbc.psd2.data.tenN.pojo;

public class GetAccountsPoJo {

    public String customerNumber;
    public String phoneNumber;
    public String nationalIdNumber;
    public String merchant;

    public GetAccountsPoJo(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public GetAccountsPoJo(String customerNumber, String phoneNumber, String nationalIdNumber, String merchant) {
        this.customerNumber = customerNumber;
        this.phoneNumber = phoneNumber;
        this.nationalIdNumber = nationalIdNumber;
        this.merchant = merchant;
    }

    @Override
    public String toString() {
        return "GetAccountsPoJo{" +
                "customerNumber='" + customerNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", nationalIdNumber='" + nationalIdNumber + '\'' +
                ", merchant='" + merchant + '\'' +
                '}';
    }
}
