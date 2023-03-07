package com.sbc.psd2.data;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class UserInfo {
  private String userID;
  private String userMail;
  private String userName;

  // sessionID or auth2 token
  private String sessionID;

  private String callerIP;

  private String otp;

  public UserInfo() {

  }

  public UserInfo(String userID, String userMail, String userName, String sessionID) {
    this.userID = userID;
    this.userMail = userMail;
    this.userName = userName;
    this.sessionID = sessionID;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getUserMail() {
    return userMail;
  }

  public void setUserMail(String userMail) {
    this.userMail = userMail;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getSessionID() {
    return sessionID;
  }

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }

  public String getCallerIP() {
    return callerIP;
  }

  public void setCallerIP(String callerIP) {
    this.callerIP = callerIP;
  }

  @Override
  public String toString() {
    return "UserInfo{" +
            "userID='" + userID + '\'' +
            ", userMail='" + userMail + '\'' +
            ", userName='" + userName + '\'' +
            ", sessionID='" + sessionID + '\'' +
            ", callerIP='" + callerIP + '\'' +
            ", otp='" + otp + '\'' +
            '}';
  }
}
