package com.sbc.psd2.data.rest;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-25
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class PsuData {
  private String password;
  private String uid;

  private String scaAuthenticationData;

  public PsuData() {
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getScaAuthenticationData() {
    return scaAuthenticationData;
  }

  public void setScaAuthenticationData(String scaAuthenticationData) {
    this.scaAuthenticationData = scaAuthenticationData;
  }
}
