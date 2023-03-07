package com.sbc.psd2.controller.impl.mb;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.IdentityManagementCommunicator;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.rest.util.Util;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public class MBIdentityManagementCommunicator implements IdentityManagementCommunicator {
  public static final String URL;

  static {
    String endPoint = AbstractCommunicatorFactory.getInstance().getIdentityManagementCommunicatorEndPoint();

    URL = endPoint + "/THE_HUB/Home";
  }

  public UserInfo getUserByToken(String token, String eIDASThumbprint) throws ApplicationException {
    LogManager.trace(getClass(), "isValidToken()", token, eIDASThumbprint);

    String params = "Sess=" + token + "&" + "Thumb="+ eIDASThumbprint;
    //System.out.println(params);
    String answer = null;
    try {
      answer = Util.doPostSync(URL, params, "application/x-www-form-urlencoded");

      LogManager.trace(getClass(), "isValidToken()", answer);
    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }

    checkAnswer(answer);
    return null;
  }

  private static void checkAnswer(String answer) throws ApplicationException {
    if (answer.contains("<ERRORS>")) {
      throw new ApplicationException(ApplicationException.TOKEN_INVALID, "Not valid PSU_ID/token presented!");
    }

    String token = null;
    String customerID = null;

    int index = answer.indexOf("<SESSION");
    if (index >=0) {
      int index2 = answer.indexOf(">", index + "<SESSION".length());

      if (index2 >= 0) {
        String attrString = answer.substring(index + "<SESSION".length(), index2).trim();
        String[] attrPairs = attrString.split(" ");

        for (String pair : attrPairs) {
          String[] nameVal = pair.split("=");
          if (nameVal[0].equals("id")) {
            token = nameVal[1].replaceAll("\"", "");
          }

          if (nameVal[0].equals("customerid")) {
            customerID = nameVal[1].replaceAll("\"", "");
          }
        }
      } else {
        LogManager.log(MBIdentityManagementCommunicator.class, "Can not find end > in <SESSION> tag in xml: "+ answer);

        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
      }
    } else {
      LogManager.log(MBIdentityManagementCommunicator.class, "Can not find <SESSION> tag in xml: "+ answer);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }

    if (token == null) {
      LogManager.log(MBIdentityManagementCommunicator.class, "Can not find token in xml: " + answer);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }
  }


  public static void main(String[] args) {
    String sessionID = "8552c782-264e-4a3e-bf34-a09a79be07fb";
    String thumbprint = "BDDC46B84F79B0F7DF4084824728460377F11170";

    MBIdentityManagementCommunicator com = new MBIdentityManagementCommunicator();

    try {
      com.getUserByToken(sessionID, thumbprint);


    } catch (Exception e) {
      e.printStackTrace();

    }
  }
}
