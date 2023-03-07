package com.sbc.psd2.data.rest;

public class Authentication {

  private static final String AUTH_TYPE_PUSH_OTP = "PUSH_OTP";

  private static final String AUTH_METHOD_ID = "OTPSS";

  private String authenticationType;
  private String authenticationVersion;
  private String authenticationMethodId;
  private String name;
  private String explanation;


  public static final Authentication AUTHENTICATION_SBC = new Authentication(AUTH_TYPE_PUSH_OTP, "1", AUTH_METHOD_ID, "OTP Service Selection", "Sirma Business Consulting OTP Service");


  public Authentication(String authenticationType, String authenticationVersion, String authenticationMethodId, String name, String explanation) {
    this.authenticationType = authenticationType;
    this.authenticationVersion = authenticationVersion;
    this.authenticationMethodId = authenticationMethodId;
    this.name = name;
    this.explanation = explanation;
  }

  public Authentication(String authenticationType, String authenticationVersion, String authenticationMethodId) {
    this.authenticationType = authenticationType;
    this.authenticationVersion = authenticationVersion;
    this.authenticationMethodId = authenticationMethodId;
  }
}
