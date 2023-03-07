package com.sbc.psd2.config;

import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Implementation;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-1-17
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */
public class AppConfig {
  private boolean testMode;

  private boolean isImmediateTransaction = false;

  private Implementation implementation;

  public AppConfig() {

  }

  public AppConfig(boolean testMode,Implementation implementation,Boolean isImmediateTransaction) {
    LogManager.trace(getClass(), "AppConfig()", "" + testMode, implementation.toString());

    this.testMode = testMode;
    this.implementation = implementation;
    this.isImmediateTransaction = isImmediateTransaction;
  }

  public boolean isTestMode() {
    return testMode;
  }

  public Implementation getImplementation() {
    return implementation;
  }

  public void setImplementation(Implementation implementation) {
    this.implementation = implementation;
  }

  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  public boolean isImmediateTransaction() {
    return isImmediateTransaction;
  }

  public void setImmediateTransaction(boolean immediateTransaction) {
    isImmediateTransaction = immediateTransaction;
  }

  @Override
  public String toString() {
    return "AppConfig{" +
            "testMode=" + testMode + '\'' +
            ", implementation='" + implementation + '\'' +
            '}';
  }

  public synchronized static AppConfig buildInstance() {
    AppConfig instance = null;
    try {
      InitialContext initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      instance = (AppConfig) envCtx.lookup("conf/appConfig");



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
                Implementation.TenN, true);

    }

    LogManager.trace(AppConfig.class, "buildInstance()", instance.toString());

    return instance;
  }
}
