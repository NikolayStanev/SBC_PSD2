package com.sbc.psd2.data.rest;

import java.util.Date;

public class Transactions {

    private String transactionId;
    private String mandateId;  // e.g SEPA
    private Date bookingDate;
    private String creditorName;
    private AccountReference creditorAccount;
    private String debtorName;
    private AccountReference debtorAccount;
    private Amount transactionAmount;



    public Transactions() {

    }

    public Transactions(String transactionId, String mandateId, Date bookingDate, String creditorName, AccountReference creditorAccount, String debtorName, AccountReference debtorAccount) {
        this.transactionId = transactionId;
        this.mandateId = mandateId;
        this.bookingDate = bookingDate;
        this.creditorName = creditorName;
        this.creditorAccount = creditorAccount;
        this.debtorName = debtorName;
        this.debtorAccount = debtorAccount;
    }

    public Amount getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Amount transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMandateId() {
        return mandateId;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public AccountReference getCreditorAccount() {
        return creditorAccount;
    }

    public void setCreditorAccount(AccountReference creditorAccount) {
        this.creditorAccount = creditorAccount;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public AccountReference getDebtorAccount() {
        return debtorAccount;
    }

    public void setDebtorAccount(AccountReference debtorAccount) {
        this.debtorAccount = debtorAccount;
    }
}
