package com.sbc.psd2.data.rest;

public class Transaction {

    private Amount transactionAmount;
    private AccountDetails creditorAccountDetails;
    private AccountDetails debtorAccountDetails;

  public Transaction() {
  }

  public Transaction(Amount transactionAmount, AccountDetails creditorAccountDetails, AccountDetails debtorAccountDetails) {
    this.transactionAmount = transactionAmount;
    this.creditorAccountDetails = creditorAccountDetails;
    this.debtorAccountDetails = debtorAccountDetails;
  }

  public Amount getTransactionAmount() {
    return transactionAmount;
  }

  public void setTransactionAmount(Amount transactionAmount) {
    this.transactionAmount = transactionAmount;
  }

  public AccountDetails getCreditorAccountDetails() {
    return creditorAccountDetails;
  }

  public void setCreditorAccountDetails(AccountDetails creditorAccountDetails) {
    this.creditorAccountDetails = creditorAccountDetails;
  }

  public AccountDetails getDebtorAccountDetails() {
    return debtorAccountDetails;
  }

  public void setDebtorAccountDetails(AccountDetails debtorAccountDetails) {
    this.debtorAccountDetails = debtorAccountDetails;
  }
}
