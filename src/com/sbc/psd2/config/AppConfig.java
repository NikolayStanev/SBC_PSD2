package com.sbc.psd2.config;

import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Implementation;

import javax.naming.Context;
import javax.naming.InitialContext;

import static com.sbc.common.util.Implementation.TenN;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-1-17
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */
public class AppConfig {
  private boolean testMode;

//  private static AppConfig instance;
  private boolean immediateTransaction = false;
  private String coreSystemCommunicatorEndPoint = "https://crm-api-test.10npay.com/api/v1/";
  private String identityManagementCommunicatorEndPoint = "https://auth-test.10npay.com/connect";

  private String scaCommunicatorEndPoint ="http://172.21.139.10:8080/OTPSS_10n/Sign"; //"https://172.16.51.97/OTPSS_10n/Sign";
  private String scaUser = "buts";
  private String scaPassword = "pass1234";
  private String implementation;

  public AppConfig() {

  }

  public AppConfig(boolean testMode,String implementation,Boolean immediateTransaction) {
    LogManager.trace(getClass(), "AppConfig()", "" + testMode, implementation.toString());

    this.testMode = testMode;
    this.implementation = implementation;
    this.immediateTransaction = immediateTransaction;
  }

  @Override
  public String toString() {
    return "AppConfig{" +
            "testMode=" + testMode +
            ", isImmediateTransaction=" + immediateTransaction +
            ", coreSystemCommunicatorEndPoint='" + coreSystemCommunicatorEndPoint + '\'' +
            ", identityManagementCommunicatorEndPoint='" + identityManagementCommunicatorEndPoint + '\'' +
            ", scaCommunicatorEndPoint='" + scaCommunicatorEndPoint + '\'' +
            ", scaUser='" + scaUser + '\'' +
            ", scaPassword='" + scaPassword + '\'' +
            ", implementation=" + implementation +
            '}';
  }

  public synchronized static AppConfig getInstance() {

    AppConfig instance = null;
    try {
      InitialContext initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      instance = (AppConfig) envCtx.lookup("conf/appConfig11");



    } catch (Exception e) {
      LogManager.log(AppConfig.class, e);

//      instance = new AppConfig(true,
//              "DECOUPLED",
//              "com.sbc.psd2.controller.impl.DummyCoreSystemCommunicator",
//              "com.sbc.psd2.controller.impl.DummyIdentityManagementCommunicator",
//              "com.sbc.psd2.controller.impl.DummySCACommunicator",
//              //"http://172.16.51.121:8080", "http://172.16.51.121:8080", "http://172.16.51.121:8080");
//              //"http://172.16.52.47:8080", "http://172.16.52.47:8080", "http://172.16.52.47:8080");
//              "https://sandbox.municipalbank.bg", "https://sandbox.municipalbank.bg", "https://sandbox.municipalbank.bg",
//              "com.sbc.psd2.controller.impl.tenN");

        instance = new AppConfig(true,
                Implementation.TenN.toString(), true);

    }

    LogManager.trace(AppConfig.class, "buildInstance()", instance.toString());

    return instance;
  }

//  public static AppConfig getInstance() {
//
//    if(instance == null) {
//      return buildInstance();
//    }
//
//    return instance;
//
//  }



  public boolean isTestMode() {
    return testMode;
  }

  public String getImplementation() {
    return implementation;
  }

  public void setImplementation(String implementation) {
    this.implementation = implementation;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public boolean isImmediateTransaction() {
    return immediateTransaction;
  }

  public void setImmediateTransaction(boolean immediateTransaction) {
    this.immediateTransaction = immediateTransaction;
  }

  public String getCoreSystemCommunicatorEndPoint() {
    return coreSystemCommunicatorEndPoint;
  }

  public void setCoreSystemCommunicatorEndPoint(String coreSystemCommunicatorEndPoint) {
    this.coreSystemCommunicatorEndPoint = coreSystemCommunicatorEndPoint;
  }

  public String getIdentityManagementCommunicatorEndPoint() {
    return identityManagementCommunicatorEndPoint;
  }

  public void setIdentityManagementCommunicatorEndPoint(String identityManagementCommunicatorEndPoint) {
    this.identityManagementCommunicatorEndPoint = identityManagementCommunicatorEndPoint;
  }

  public String getScaCommunicatorEndPoint() {
    return scaCommunicatorEndPoint;
  }

  public void setScaCommunicatorEndPoint(String scaCommunicatorEndPoint) {
    this.scaCommunicatorEndPoint = scaCommunicatorEndPoint;
  }

  public String getScaUser() {
    return scaUser;
  }

  public void setScaUser(String scaUser) {
    this.scaUser = scaUser;
  }

  public String getScaPassword() {
    return scaPassword;
  }

  public void setScaPassword(String scaPassword) {
    this.scaPassword = scaPassword;
  }





}
