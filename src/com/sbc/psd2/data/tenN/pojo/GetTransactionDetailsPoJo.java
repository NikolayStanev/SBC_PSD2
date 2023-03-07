package com.sbc.psd2.data.tenN.pojo;

public class GetTransactionDetailsPoJo {

    public String transactionReferenceNumber;
    public String customerNumber;

    public GetTransactionDetailsPoJo(String transactionReferenceNumber, String customerNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
        this.customerNumber = customerNumber;
    }


    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
}
