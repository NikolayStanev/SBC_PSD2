package com.sbc.psd2;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-25
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizeResponse {
  private String resourceStatus;

  public AuthorizeResponse(String transactionStatus) {
    this.resourceStatus = transactionStatus;
  }

  public String getResourceStatus() {
    return resourceStatus;
  }

  public void setResourceStatus(String resourceStatus) {
    this.resourceStatus = resourceStatus;
  }
}
