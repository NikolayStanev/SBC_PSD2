package com.sbc.psd2.data.tenN;

public class TenNTaxes {

    public String accountNo;
    public int accountBalance;
    public String accountCurrency;
    public int transactionAmount;
    public String transactionCurrency;
    public int transactionFee;
    public String transactionFeeCurrency;

    public TenNTaxes() {
    }

    public TenNTaxes(String accountNo, int accountBalance, String accountCurrency, int transactionAmount, String transactionCurrency, int transactionFee, String transactionFeeCurrency) {
        this.accountNo = accountNo;
        this.accountBalance = accountBalance;
        this.accountCurrency = accountCurrency;
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.transactionFee = transactionFee;
        this.transactionFeeCurrency = transactionFeeCurrency;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public int getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(int transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String getTransactionFeeCurrency() {
        return transactionFeeCurrency;
    }

    public void setTransactionFeeCurrency(String transactionFeeCurrency) {
        this.transactionFeeCurrency = transactionFeeCurrency;
    }
}
