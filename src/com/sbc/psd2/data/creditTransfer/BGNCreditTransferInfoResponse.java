package com.sbc.psd2.data.creditTransfer;

import com.sbc.psd2.data.rest.AccountDetails;
import com.sbc.psd2.data.rest.Amount;
import com.sbc.psd2.data.rest.PaymentType;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-26
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class BGNCreditTransferInfoResponse {
  // Mandatory
  private Amount instructedAmount;
  private AccountDetails debtorAccount;
  private String creditorName;
  private AccountDetails creditorAccount;

  private String transactionStatus;
  private String paymentId;

  // Optional
  private PaymentType paymentType;
  private String remittanceInformationUnstructured;

  public BGNCreditTransferInfoResponse() {
  }

  public BGNCreditTransferInfoResponse(Amount instructedAmount, AccountDetails debtorAccount,
                                       String creditorName, AccountDetails creditorAccount, String transactionStatus,
                                       String paymentId, PaymentType paymentType, String remittanceInformationUnstructured) {
    this.instructedAmount = instructedAmount;
    this.debtorAccount = debtorAccount;
    this.creditorName = creditorName;
    this.creditorAccount = creditorAccount;
    this.transactionStatus = transactionStatus;
    this.paymentId = paymentId;
    this.paymentType = paymentType;
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
  }

  public static BGNCreditTransferInfoResponse buildFromOp(BGNCreditTransferOp op) {
    return new BGNCreditTransferInfoResponse(op.getInstructedAmount(), op.getDebtorAccount(),
            op.getCreditorName(), op.getCreditorAccount(), op.getTransactionStatus(),
            op.getPaymentId(), op.getPaymentType(), op.getRemittanceInformationUnstructured());
  }

  public Amount getInstructedAmount() {
    return instructedAmount;
  }

  public void setInstructedAmount(Amount instructedAmount) {
    this.instructedAmount = instructedAmount;
  }


  public String getCreditorName() {
    return creditorName;
  }

  public void setCreditorName(String creditorName) {
    this.creditorName = creditorName;
  }

  public AccountDetails getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(AccountDetails debtorAccount) {
    this.debtorAccount = debtorAccount;
  }

  public AccountDetails getCreditorAccount() {
    return creditorAccount;
  }

  public void setCreditorAccount(AccountDetails creditorAccount) {
    this.creditorAccount = creditorAccount;
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

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public String getRemittanceInformationUnstructured() {
    return remittanceInformationUnstructured;
  }

  public void setRemittanceInformationUnstructured(String remittanceInformationUnstructured) {
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
  }
}
