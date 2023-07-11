package com.sbc.psd2.data.creditTransfer;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Util;
import com.sbc.psd2.data.PSD2RequestCommonData;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.creditTransfer.dao.BGNCreditTransferOpDAO;
import com.sbc.psd2.data.rest.AccountDetails;
import com.sbc.psd2.data.rest.Amount;
import com.sbc.psd2.data.rest.PaymentType;
import com.sbc.psd2.data.statuses.TransactionStatuses;
import com.sbc.psd2.controller.UserFilter;

import java.util.Date;
import java.util.UUID;


/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-23
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class BGNCreditTransferOp {
  private int dbID = -1;
  private String extRefID = null;
  private Date logDate = null;

  // Mandatory
  private Amount instructedAmount;
  private AccountDetails debtorAccount;

  private String debtorPhoneNumber;
  private String creditorName;
  private AccountDetails creditorAccount;

  private String transactionStatus;
  private String paymentId;

  // Optional
  private PaymentType paymentType;
  private String remittanceInformationUnstructured;

  private String customerNumber = "0";

  private String transactionFee;

  private String transactionFeeCurrency;


  private PSD2RequestCommonData commonData;

  public BGNCreditTransferOp(int dbID, String extRefID, Date logDate, Amount instructedAmount,
                             AccountDetails debtorAccount, String creditorName,
                             AccountDetails creditorAccount, String transactionStatus,
                             String paymentId, PaymentType paymentType,
                             String remittanceInformationUnstructured,
                             String customerNumber,
                             String transactionFee,
                             String transactionFeeCurrency,
                             PSD2RequestCommonData commonData) {
    this.dbID = dbID;
    this.extRefID = extRefID;
    this.logDate = logDate;
    this.instructedAmount = instructedAmount;
    this.debtorAccount = debtorAccount;
    this.creditorName = creditorName;
    this.creditorAccount = creditorAccount;
    this.transactionStatus = transactionStatus;
    this.paymentId = paymentId;
    this.paymentType = paymentType;
    this.remittanceInformationUnstructured = remittanceInformationUnstructured;
    this.customerNumber = customerNumber;
    this.transactionFee = transactionFee;
    this.transactionFeeCurrency = transactionFeeCurrency;
    this.commonData = commonData;
  }

  public static BGNCreditTransferOp buildBGNCreditTransferOp(BGNCreditTransferRequest request, UserInfo userInfo) {
    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();

    PSD2RequestCommonData commonInfo = new PSD2RequestCommonData(request.getxRequestID(), request.getPsuID(),
            request.getPsuIPAddress(), userInfo.getCallerIP(), request.getConsentID(), tppID);

    String paymentId = UUID.randomUUID().toString();
    String transactionStatus = TransactionStatuses.RECEIVED;

    BGNCreditTransferOp op = new BGNCreditTransferOp(-1, null, null, request.getInstructedAmount(), request.getDebtorAccount(),
            request.getCreditorName(), request.getCreditorAccount(), transactionStatus,
            paymentId, request.getPaymentType(), request.getRemittanceInformationUnstructured(),null,null,null, commonInfo);

    return op;
  }

  public PSD2RequestCommonData getCommonData() {
    return commonData;
  }

  public void setCommonData(PSD2RequestCommonData commonData) {
    this.commonData = commonData;
  }

  public int getDbID() {
    return dbID;
  }

  public void setDbID(int dbID) {
    this.dbID = dbID;
  }

  public String getExtRefID() {
    return extRefID;
  }

  public void setExtRefID(String extRefID) {
    this.extRefID = extRefID;
  }

  public Date getLogDate() {
    return logDate;
  }

  public void setLogDate(Date logDate) {
    this.logDate = logDate;
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

  public String getDebtorPhoneNumber() {
    return debtorPhoneNumber;
  }

  public void setDebtorPhoneNumber(String debtorPhoneNumber) {
    this.debtorPhoneNumber = debtorPhoneNumber;
  }

  public String getTransactionFee() {
    return transactionFee;
  }

  public void setTransactionFee(String transactionFee) {
    this.transactionFee = transactionFee;
  }

  public String getTransactionFeeCurrency() {
    return transactionFeeCurrency;
  }

  public void setTransactionFeeCurrency(String transactionFeeCurrency) {
    this.transactionFeeCurrency = transactionFeeCurrency;
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

  public String getCustomerNumber() {
    return customerNumber;
  }

  public void setCustomerNumber(String customerNumber) {
    this.customerNumber = customerNumber;
  }

  public static BGNCreditTransferOp getAndCheckOp(String paymentId, String tppID) throws ApplicationException {
    BGNCreditTransferOp op = BGNCreditTransferOpDAO.getOpByPaymentID(paymentId, tppID);

    if (op == null) {
      LogManager.trace(BGNCreditTransferOp.class, "getAndCheckOp", "There is no transaction with that ID: " + paymentId + ", tppID = " + tppID);

      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "There is no transaction with that ID!");
    }

    checkOp(op);

    return op;
  }

  public static void checkOp(BGNCreditTransferOp op) throws ApplicationException {
    if (op.getTransactionStatus().equalsIgnoreCase(TransactionStatuses.REJECTED)) {
      throw new ApplicationException(ApplicationException.RESOURCE_EXPIRED_PATH, "Rejected resource!");
    }

    if (op.getTransactionStatus().equalsIgnoreCase(TransactionStatuses.RECEIVED) && Util.isTransactionExpired(op.getLogDate())) {
      LogManager.trace(BGNCreditTransferOp.class, "checkOp", "Expired by time: " + op.getPaymentId());

      op.setTransactionStatus(TransactionStatuses.REJECTED);
      BGNCreditTransferOpDAO.update(op);
      throw new ApplicationException(ApplicationException.RESOURCE_EXPIRED_PATH, "Expired resource! Time for authorization is passed!");
    }
  }

//  public static void checkAccountOwnership(BGNCreditTransferOp op, UserInfo userInfo) throws ApplicationException {
//    AccountDetails accountDetails = op.getDebtorAccount();
//    boolean found = false;
//
//    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
//    ArrayList<CoreSystemAccountInfo> accounts = csCommunicator.getAccounts(userInfo);
//    for (CoreSystemAccountInfo accountInfo : accounts) {
//      if (accountDetails.getIban().getIban().equalsIgnoreCase(accountInfo.getIban())) {
//        found = true;
//        break;
//      }
//    }
//
//    if (!found) {
//      throw new ApplicationException(ApplicationException.RESOURCE_EXPIRED_PATH, "Rejected resource!");
//    }
//  }

  @Override
  public String toString() {
    return commonData.getxRequestID() + ", " +
           commonData.getPsuIPAddress() + ", " +
           getDebtorAccount().getIban() + ", " +
           getInstructedAmount().getContent() + ", " +
           getInstructedAmount().getCurrency() + ", " +
           getCreditorAccount().getIban() + ", " +
           getCreditorName() + ", " +
           getTransactionStatus() + ", " +
           getPaymentId();
  }
}
