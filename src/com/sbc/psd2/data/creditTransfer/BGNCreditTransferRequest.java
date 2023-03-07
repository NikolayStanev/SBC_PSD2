package com.sbc.psd2.data.creditTransfer;


import com.sbc.psd2.data.rest.AccountDetails;
import com.sbc.psd2.data.rest.Amount;
import com.sbc.psd2.data.PSD2RequestCommonData;
import com.sbc.psd2.data.rest.PaymentType;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-22
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class BGNCreditTransferRequest extends PSD2RequestCommonData {
  // Mandatory
  private Amount instructedAmount;
  private AccountDetails debtorAccount;
  private String creditorName;
  private AccountDetails creditorAccount;

  // Optional
  private PaymentType paymentType;
  private String remittanceInformationUnstructured;

  public BGNCreditTransferRequest() {
  }

  public BGNCreditTransferRequest(String xRequestID, String psuID, String psuIP, String ip, Amount instructedAmount,
                                  AccountDetails debtorAccount, String creditorName, AccountDetails creditorAccount,
                                  PaymentType paymentType, String remittanceInformationUnstructured, String consentID, String tppID) {
    super(xRequestID, psuID, psuIP, ip, consentID, tppID);

    this.instructedAmount = instructedAmount;
    this.debtorAccount = debtorAccount;
    this.creditorName = creditorName;
    this.creditorAccount = creditorAccount;
    this.paymentType = paymentType;
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
  }

  public Amount getInstructedAmount() {
    return instructedAmount;
  }

  public void setInstructedAmount(Amount instructedAmount) {
    this.instructedAmount = instructedAmount;
  }

  public AccountDetails getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(AccountDetails debtorAccount) {
    this.debtorAccount = debtorAccount;
  }

  public String getCreditorName() {
    return creditorName;
  }

  public void setCreditorName(String creditorName) {
    this.creditorName = creditorName;
  }

  public AccountDetails getCreditorAccount() {
    return creditorAccount;
  }

  public void setCreditorAccount(AccountDetails creditorAccount) {
    this.creditorAccount = creditorAccount;
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

  @Override
  public String toString() {
    return "BGNCreditTransferRequest{" +
            "instructedAmount=" + instructedAmount +
            ", debtorAccount=" + debtorAccount +
            ", creditorName='" + creditorName + '\'' +
            ", creditorAccount=" + creditorAccount +
            ", paymentType=" + paymentType +
            ", remittanceInformationUnstructured='" + remittanceInformationUnstructured + '\'' +
            '}';
  }
}
