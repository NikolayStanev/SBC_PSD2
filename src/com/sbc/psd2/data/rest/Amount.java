package com.sbc.psd2.data.rest;

import java.math.BigDecimal;

public class Amount {

  private String currency;
  private BigDecimal content;

  public Amount() {

  }

  public Amount(String currency, BigDecimal content) {
    this.currency = currency;
    this.content = content;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }


  public String getCurrency() {
    return currency;
  }

  public BigDecimal getContent() {
    return content;
  }

  public void setContent(BigDecimal content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Amount{" +
            "currency='" + currency + '\'' +
            ", amount='" + content + '\'' +
            '}';
  }
}
