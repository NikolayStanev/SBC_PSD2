package com.sbc.psd2.data.rest;

import java.util.Date;

public class Balance {

  private String balanceType;
  private Amount balanceAmount;

  // Conditional
  private Date referenceDate;
  private Date lastChangeDateTime;

  public Balance() {
  }

  public Balance(String balanceType, Amount balanceAmount) {
    this.balanceType = balanceType;
    this.balanceAmount = balanceAmount;
  }

  public String getBalanceType() {
    return balanceType;
  }

  public void setBalanceType(String balanceType) {
    this.balanceType = balanceType;
  }

  public Amount getBalanceAmount() {
    return balanceAmount;
  }

  public void setBalanceAmount(Amount balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public Date getReferenceDate() {
    return referenceDate;
  }

  public void setReferenceDate(Date referenceDate) {
    this.referenceDate = referenceDate;
  }

  public Date getLastChangeDateTime() {
    return lastChangeDateTime;
  }

  public void setLastChangeDateTime(Date lastChangeDateTime) {
    this.lastChangeDateTime = lastChangeDateTime;
  }
}
