package com.sbc.psd2.data;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-23
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class PSD2RequestCommonData {
  private String xRequestID;
  private String psuID;
  private String psuIPAddress;
  private String ip;
  private String consentID;
  private String tppID;


  public PSD2RequestCommonData() {
  }

  public PSD2RequestCommonData(String xRequestID, String psuID, String psuIPAddress, String ip, String consentID, String tppID) {
    this.xRequestID = xRequestID;
    this.psuID = psuID;
    this.psuIPAddress = psuIPAddress;
    this.ip = ip;
    this.consentID = consentID;
    this.tppID = tppID;
  }

  public String getxRequestID() {
    return xRequestID;
  }

  public String getPsuIPAddress() {
    return psuIPAddress;
  }

  public String getIp() {
    return ip;
  }

  public void setxRequestID(String xRequestID) {
    this.xRequestID = xRequestID;
  }

  public void setPsuIPAddress(String psuIPAddress) {
    this.psuIPAddress = psuIPAddress;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPsuID() {
    return psuID;
  }

  public void setPsuID(String psuID) {
    this.psuID = psuID;
  }

  public String getConsentID() {
    return consentID;
  }

  public void setConsentID(String consentID) {
    this.consentID = consentID;
  }

  public String getTppID() {
    return tppID;
  }

  public void setTppID(String tppID) {
    this.tppID = tppID;
  }


  @Override
  public String toString() {
    return "PSD2RequestCommonData{" +
            "xRequestID='" + xRequestID + '\'' +
            ", psuIPAddress='" + psuIPAddress + '\'' +
            ", ip='" + ip + '\'' +
            ", tppID='" + tppID + '\'' +
            '}';
  }
}
