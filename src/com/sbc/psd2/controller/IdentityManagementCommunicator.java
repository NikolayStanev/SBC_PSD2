package com.sbc.psd2.controller;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.data.UserInfo;

import java.net.MalformedURLException;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 10:17
 * To change this template use File | Settings | File Templates.
 */
public interface IdentityManagementCommunicator {
  public UserInfo getUserByToken(String token, String eIDASThumbprint) throws ApplicationException;
}
