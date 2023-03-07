package com.sbc.psd2.controller.impl;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.controller.IdentityManagementCommunicator;
import com.sbc.psd2.data.UserInfo;

public class DummyIdentityManagementCommunicator implements IdentityManagementCommunicator{

  public UserInfo getUserByToken(String token, String eIDASThumbprint) throws ApplicationException {
    return new UserInfo("7788990011", "ivanpetrov@dummy.com", "Ivan Petrov", token);
  }
}
