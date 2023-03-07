package com.sbc.psd2.data.rest;

import com.sbc.psd2.data.account.AccountLinks;

public class AccountDetails {

  private IBAN iban;
  private String bban;
  private String pan;
  private String maskedPan;
  private String msisdn;
  private String resourceId;
  private String name;
  private String product;
  private String cashAccountType;
  private String currency;

  private AccountLinks _links;

  public AccountDetails() {
  }

  public AccountDetails(IBAN iban) {
    this.iban = iban;
  }

  public IBAN getIban() {
    return iban;
  }

  public void setIban(IBAN iban) {
    this.iban = iban;
  }

  public String getBban() {
    return bban;
  }

  public void setBban(String bban) {
    this.bban = bban;
  }

  public String getPan() {
    return pan;
  }

  public void setPan(String pan) {
    this.pan = pan;
  }

  public String getMaskedPan() {
    return maskedPan;
  }

  public void setMaskedPan(String maskedPan) {
    this.maskedPan = maskedPan;
  }

  public String getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getCashAccountType() {
    return cashAccountType;
  }

  public void setCashAccountType(String cashAccountType) {
    this.cashAccountType = cashAccountType;
  }

  public AccountLinks get_links() {
    return _links;
  }

  public void set_links(AccountLinks _links) {
    this._links = _links;
  }



  @Override
  public String toString() {
    return "AccountDetails{" +
            "iban='" + iban + '\'';
//    +
//            ", bban='" + bban + '\'' +
//            ", pan='" + pan + '\'' +
//            ", maskedPan='" + maskedPan + '\'' +
//            ", msisdn='" + msisdn + '\'' +
//            ", currency='" + currency + '\'' +
//            '}';
  }
}
