package com.sbc.test.hub;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-4-9
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class HUBLoginAnswer {
  private static final String SESSION_ID_MARKER = "name=\"Sess\" value=\"";
  private static final String IBANS_MARKER = "name=\"accounts\" value=\"";

  private String sessionID;
  private ArrayList<String> ibans;

  public HUBLoginAnswer(String sessionID, ArrayList<String> ibans) {
    this.sessionID = sessionID;
    this.ibans = ibans;
  }

  public static HUBLoginAnswer build(String html) {
    String sessionID = null;
    ArrayList<String> ibans = new ArrayList<>();

    int index = html.indexOf(SESSION_ID_MARKER);
    if (index >= 0) {
      int index2 = html.indexOf("\"", index + SESSION_ID_MARKER.length() + 1);

      if (index2 >= 0 ) {
        sessionID = html.substring(index + SESSION_ID_MARKER.length(), index2);
      }
    }

    String ibansString = null;
    index = html.indexOf(IBANS_MARKER);
    if (index >= 0) {
      int index2 = html.indexOf("\"", index + IBANS_MARKER.length() + 1);

      if (index2 >= 0 ) {
        ibansString = html.substring(index + IBANS_MARKER.length(), index2);
      }
    }

    if (ibansString != null) {
      String[] items = ibansString.split(",");
      for (String item : items) {
        if (item != null && !item.equals(""))
        ibans.add(item);
      }
    }


    HUBLoginAnswer hubLoginAnswer = new HUBLoginAnswer(sessionID, ibans);

    return hubLoginAnswer;
  }

  public String getSessionID() {
    return sessionID;
  }

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

  public ArrayList<String> getIbans() {
    return ibans;
  }

  public void setIbans(ArrayList<String> ibans) {
    this.ibans = ibans;
  }
}
