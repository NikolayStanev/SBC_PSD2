package com.sbc.psd2.data.consent;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-22
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class ConsentStatusResponse {
  private String status;

  public ConsentStatusResponse(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
