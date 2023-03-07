package com.sbc.psd2.data.rest;


public class AccountReference {
  private String iban;
  private String currency;

  public AccountReference() {
  }

  public AccountReference(String iban) {
    this.iban = iban;
  }

  public AccountReference(String iban, String currency) {
    this.iban = iban;
    this.currency = currency;
  }


  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
