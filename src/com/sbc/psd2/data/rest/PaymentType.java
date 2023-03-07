package com.sbc.psd2.data.rest;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-22
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class PaymentType {
  private String serviceLevel;

  public PaymentType() {
  }

  public PaymentType(String serviceLevel) {
    this.serviceLevel = serviceLevel;
  }

  public String getServiceLevel() {
    return serviceLevel;
  }

  public void setServiceLevel(String serviceLevel) {
    this.serviceLevel = serviceLevel;
  }

  @Override
  public String toString() {
    return "PaymentType{" +
            "serviceLevel='" + serviceLevel + '\'' +
            '}';
  }
}
