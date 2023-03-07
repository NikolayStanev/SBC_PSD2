package com.sbc.psd2.data.creditTransfer;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-25
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public class BGNCreditTransferStatusResponse {
  private String transactionStatus;

  public BGNCreditTransferStatusResponse() {
  }

  public BGNCreditTransferStatusResponse(String transactionStatus) {
    this.transactionStatus = transactionStatus;
  }

  public String getTransactionStatus() {
    return transactionStatus;
  }

  public void setTransactionStatus(String transactionStatus) {
    this.transactionStatus = transactionStatus;
  }
}
