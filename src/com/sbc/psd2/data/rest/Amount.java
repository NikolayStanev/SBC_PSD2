package com.sbc.psd2.data.rest;

import java.math.BigDecimal;

public class Amount {

  private String currency;
  private BigDecimal amount;

  public Amount() {

  }

  public Amount(String currency, BigDecimal content) {
    this.currency = currency;
    this.amount = content;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }


  public String getCurrency() {
    return currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "Amount{" +
            "currency='" + currency + '\'' +
            ", amount='" + amount + '\'' +
            '}';
  }
}
