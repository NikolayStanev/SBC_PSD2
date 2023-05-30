package com.sbc.psd2.data.creditTransfer;
import com.sbc.psd2.data.rest.Amount;
import com.sbc.psd2.data.rest.Authentication;


/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-22
 * Time: 15:39
 * To change this template use File | Settings | File Templates.
 */
public class BGNCreditTransferResponse {
   private String transactionStatus;
   private String paymentId;
   private Authentication chosenScaMethod = Authentication.AUTHENTICATION_SBC;
   private GetCreditTransferLinks _links;
  private Amount transactionFees;
  private boolean transactionFeeIndicator;

  public BGNCreditTransferResponse() {
  }

  public BGNCreditTransferResponse(String transactionStatus, String paymentId) {
    this(transactionStatus, paymentId, null);
  }

  public BGNCreditTransferResponse(String transactionStatus, String paymentId, GetCreditTransferLinks _links) {
    this.transactionStatus = transactionStatus;
    this.paymentId = paymentId;
    this._links = _links;
  }
  public BGNCreditTransferResponse(String transactionStatus, String paymentId, GetCreditTransferLinks _links, Amount transactionFees, boolean transactionFeeIndicator) {
    this.transactionStatus = transactionStatus;
    this.paymentId = paymentId;
    this._links = _links;
    this.transactionFees = transactionFees;
    this.transactionFeeIndicator = transactionFeeIndicator;
  }

  public static BGNCreditTransferResponse buildResponse(BGNCreditTransferOp op) {
    String paymentId = op.getPaymentId();
    String transactionStatus = op.getTransactionStatus();

    String selfLink = "/v1/payments/domestic-credit-transfers-bgn/"+paymentId;
    String statusLink = "/v1/payments/domestic-credit-transfers-bgn/"+paymentId+"/status";
    String authorizeLink = "/v1/payments/domestic-credit-transfers-bgn/"+paymentId + "/authorisations";

    GetCreditTransferLinks links = new GetCreditTransferLinks(selfLink, statusLink, authorizeLink);

    BGNCreditTransferResponse response = new BGNCreditTransferResponse(transactionStatus, paymentId, links);

    return response;
  }

  public String getTransactionStatus() {
    return transactionStatus;
  }

  public void setTransactionStatus(String transactionStatus) {
    this.transactionStatus = transactionStatus;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public GetCreditTransferLinks get_links() {
    return _links;
  }

  public void set_links(GetCreditTransferLinks _links) {
    this._links = _links;
  }

  @Override
  public String toString() {
    return "BGNCreditTransferResponse{" +
            "transactionStatus='" + transactionStatus + '\'' +
            ", paymentId='" + paymentId + '\'' +
            ", chosenScaMethod=" + chosenScaMethod +
            ", _links=" + _links +
            ", transactionFees=" + transactionFees +
            ", transactionFeeIndicator=" + transactionFeeIndicator +
            '}';
  }
}
