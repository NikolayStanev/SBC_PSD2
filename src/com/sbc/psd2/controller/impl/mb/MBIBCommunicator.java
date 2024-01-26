package com.sbc.psd2.controller.impl.mb;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.impl.mb.util.XmlParserMBIB;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.controller.impl.mb.util.MBIBCodesMapper;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.mb.MbCoreSystemAccountInfo;
import com.sbc.psd2.data.rest.*;
import com.sbc.psd2.controller.UserFilter;
import com.sbc.psd2.rest.util.Util;
import oracle.jdbc.OracleTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
public class MBIBCommunicator implements CoreSystemCommunicator {
  private static final String URL_PAYMENT_OTP;
  private static final String URL_ENQUIRY;

  private static final String PARAM_ACCOUNT_ID = "[ACCOUNT_ID]";

  private static final String PARAM_TOKEN = "[TOKEN]";
  private static final String PARAM_THUMBPRINT = "[THUMBPRINT]";
  //private static final String PARAM_TEXT_FOR_SIGNING = "[TEXT_FOR_SIGNING]";
  private static final String PARAM_OTP = "[OTP]";

  private static final String PARAM_FROM_DATE = "[FROM_DATE]";
  private static final String PARAM_TO_DATE = "[TO_DATE]";

  private static final String PARAM_DOC_ID = "[DOC_ID]";
  private static final String PARAM_PAYEE_NAME = "[PAYEE_NAME]";
  private static final String PARAM_CREDIT_IBAN = "[CREDIT_IBAN]";
  private static final String PARAM_DEBIT_IBAN = "[DEBIT_IBAN]";
  private static final String PARAM_CURRENCY = "[CURRENCY]";
  private static final String PARAM_AMOUNT = "[AMOUNT]";
  private static final String PARAM_REASON = "[REASON]";

  private static final String MB_process = "{call app.PSD2_ENQUIRY.process(?,?)}";

  private static final String TRANSACTION_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
          "<PACKAGE sessid=\"[TOKEN]\" langid=\"BG\" action=\"confirm\" thumb=\"[THUMBPRINT]\" tokenotpss=\"[OTP]\" id=\"p1\" massPay=\"false\">\n" +
          "<DOCUMENTS>" +
          "<DOCUMENT " +
          "id=\"[DOC_ID]\" " +
          "funcid=\"161\" " +
          "namekt=\"[PAYEE_NAME]\" " +
          "ibankt=\"[CREDIT_IBAN]\" " +
          "ibandt=\"[DEBIT_IBAN]\" " +
          "currkt=\"[CURRENCY]\" " +
          "amount=\"[AMOUNT]\" " +
          "reason=\"[REASON]\" " +
          "more=\"\" " +
          "bickt=\"\" " +
          "country=\"\" " +
          "addresskt2=\"\" " +
          "addresskt=\"\" " +
          "paytype=\"\" " +
          "feedt=\"\" " +
          "system=\"\"" +
          "/></DOCUMENTS>" +
          "</PACKAGE>";


  static {
    String endPoint = AppConfig.getInstance().getCoreSystemCommunicatorEndPoint();


    URL_PAYMENT_OTP = endPoint + "/THE_HUB/Package";
    URL_ENQUIRY = endPoint + "/THE_HUB/Enquiry";
  }

//cdf
  private static final String READ_TRANSACTION_DETAILS_XML= "<PSD2_ENQUIRY thumb=\"[THUMBPRINT]\" lang=\"BG\">" +
          "<DOCDETAILS docid=\"[DOC_ID]\"/>" +
          "</PSD2_ENQUIRY>";

  private static final String READ_TRANSACTION_LIST_XML = "<PSD2_ENQUIRY thumb=\"[THUMBPRINT]\" lang=\"BG\">" +
          "<DOCUMENTS datefrom=\"[FROM_DATE]\" dateto=\"[TO_DATE]\" iban=\"[ACCOUNT_ID]\"/>" +
          "</PSD2_ENQUIRY>";
  private static final String ACCOUNT_DETAILS_XML = "<PSD2_ENQUIRY thumb=\"[THUMBPRINT]\" lang=\"BG\">" +
          "<ACCOUNT iban=\"[ACCOUNT_ID]\"/>" +
          "</PSD2_ENQUIRY>";


  private static final String GET_TRANSACTION_STATUS = "<PSD2_ENQUIRY thumb=\"[THUMBPRINT]\" lang=\"BG\">" +
          "<DOCDETAILS docid=\"[DOC_ID]\"/>" +
          "</PSD2_ENQUIRY>";


  public String getTransactionStatus(BGNCreditTransferOp op) throws ApplicationException {
    LogManager.trace(getClass(), "getTransactionStatus()", op.getExtRefID());
    String refID = op.getExtRefID();
    String status = null;

//    String thumbprint = "BDDC46B84F79B0F7DF4084824728460377F11170";
    String thumbprint = UserFilter.getEIDASInfo().getThumbprint();

    Connection connection = null;
    CallableStatement ocs = null;

    try {
      String xml = GET_TRANSACTION_STATUS.replace(PARAM_THUMBPRINT, thumbprint);
      xml = xml.replace(PARAM_DOC_ID, refID);

      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(MB_process);
      ocs.registerOutParameter(2, OracleTypes.CLOB);

      ocs.setString(1, xml);

      ocs.execute();

      String xmlResult = ocs.getString(2);


      if (xmlResult.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.FORMAT_ERROR, "Not valid iban!");
      } else {
        status = XmlParserMBIB.parseTransactionStatus(xmlResult);
        LogManager.trace(getClass(), "getTransactionStatus()", refID, "Core returns transaction status" + status);

        String psd2status = MBIBCodesMapper.getInstance().mapCoreSystemTransactionStatus(status);

        LogManager.trace(getClass(), "getTransactionStatus()", refID, "Mapped to psd2 status" + psd2status);

        return psd2status;
      }

    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(getClass(), ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(getClass(), ex);
        }
      }
    }
  }

  public ArrayList<Balance> getAccountBalances(String iban) throws ApplicationException {
    LogManager.trace(getClass(), "getAccountBalances()", iban);

    ArrayList<Balance> list = new ArrayList<>();

    Connection connection = null;
    CallableStatement ocs = null;

    String thumbprint = UserFilter.getEIDASInfo().getThumbprint();

    try {
      String xml = ACCOUNT_DETAILS_XML.replace(PARAM_THUMBPRINT, thumbprint);
      xml = xml.replace(PARAM_ACCOUNT_ID, iban);

      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(MB_process);
      ocs.registerOutParameter(2, OracleTypes.CLOB);

      ocs.setString(1, xml);

      ocs.execute();

      String xmlResult = ocs.getString(2);


      if (xmlResult.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.FORMAT_ERROR, "Not valid iban!");
      } else {
        list = XmlParserMBIB.parseBalances(xmlResult);
      }
      return list;

    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(getClass(), ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(getClass(), ex);
        }
      }
    }
  }

  public Transactions readTransactionsDetails(String transactionId) throws ApplicationException {
    LogManager.trace(getClass(), "readTransactionsDetails()", transactionId);

    Transactions transactionsDetails = new Transactions();

//    String thumbprint = "BDDC46B84F79B0F7DF4084824728460377F11170";
    String thumbprint = UserFilter.getEIDASInfo().getThumbprint();

    CallableStatement ocs = null;

    Connection connection = null;

    try {
      String xml = READ_TRANSACTION_DETAILS_XML.replace(PARAM_THUMBPRINT, thumbprint);;
      xml = xml.replace(PARAM_DOC_ID, transactionId);
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(MB_process);
      ocs.registerOutParameter(2, OracleTypes.CLOB);

      ocs.setString(1, xml);

      ocs.execute();

      String xmlResult = ocs.getString(2);

      if (xmlResult.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.FORMAT_ERROR, "Bad params!");
      } else {
        transactionsDetails = XmlParserMBIB.parseTransactionDetails(xmlResult);

        return transactionsDetails;
      }

    }catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(getClass(), ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(MBIBCommunicator.class, ex);
        }
      }
    }
  }

  public ReadTransactionsListResponse readTransactionsList(String accountId, Date dateFrom, Date dateTo, String bookingStatus) throws ApplicationException {
    LogManager.trace(getClass(), "readTransactionsList()", accountId);

    ReadTransactionsListResponse transactionsList = null;

    //String thumbprint = "BDDC46B84F79B0F7DF4084824728460377F11170";
    String thumbprint = UserFilter.getEIDASInfo().getThumbprint();

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    CallableStatement ocs = null;

    Connection connection = null;

    try {
      String xml = READ_TRANSACTION_LIST_XML.replace(PARAM_THUMBPRINT, thumbprint);
      xml = xml.replace(PARAM_FROM_DATE, sdf.format(dateFrom));
      xml = xml.replace(PARAM_TO_DATE, sdf.format(dateTo));
      xml = xml.replace(PARAM_ACCOUNT_ID, accountId);

      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(MB_process);
      ocs.registerOutParameter(2, OracleTypes.CLOB);

      ocs.setString(1, xml);

      ocs.execute();

      String xmlResult = ocs.getString(2);

      if (xmlResult.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.FORMAT_ERROR, "Bad params!");
      } else {
        transactionsList = XmlParserMBIB.parseTransactionsList(xmlResult, bookingStatus);

        return transactionsList;
      }

    }catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(getClass(), ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(MBIBCommunicator.class, ex);
        }
      }
    }

  }

  @Override
  public void validateIBANs(String iban, UserInfo userInfo) throws ApplicationException {
    boolean found = false;

    ArrayList<CoreSystemAccountInfo> accounts = getAccounts(userInfo);
    for (CoreSystemAccountInfo accountInfo : accounts) {
      if (iban.equalsIgnoreCase(accountInfo.getIban())) {
        found = true;
        break;
      }
    }

    if (!found) {
      throw new ApplicationException(ApplicationException.RESOURCE_EXPIRED_PATH, "Rejected resource!");
    }
  }

  @Override
  public void validateIBANs(HashMap<String, Account> accountMap, UserInfo userInfo) throws ApplicationException {

    ArrayList<CoreSystemAccountInfo> list = getAccounts(userInfo);

    for (String iban : accountMap.keySet()) {
      boolean found = false;
      for (CoreSystemAccountInfo info : list) {
        if (info.getIban().equalsIgnoreCase(iban)) {
          //fill in if there is branch in the coreSystem acc info.
//          if (info.getBranch() != null) {
//
//            Account account = accountMap.get(iban);
//            account.setBranch(info.getBranch());
//
//            accountMap.replace(iban,account);
//          }

          found = true;
          break;

        }
      }

      if (!found) {
        throw new ApplicationException(ApplicationException.CONSENT_INVALID, "IBANs are not owned by the same user!");
      }
    }
  }

  public boolean confirmFunds(String accIban, BigDecimal amount, String currency) throws ApplicationException {
    // todo: implement it
    boolean flag = false;

    return flag;
  }


  public String makeTransaction(BGNCreditTransferOp op) throws ApplicationException {
    LogManager.trace(getClass(), "makeTransaction()", op.toString());

    String refID = null;
    String answer = null;

    try {
      String thumbprint = UserFilter.getEIDASInfo().getThumbprint();
      String token = UserFilter.getUserInfo().getSessionID();
      String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
      String otp = UserFilter.getUserInfo().getOtp();

      String description = "PAYMENT TPP " + tppID;

      String xml = TRANSACTION_XML.replace(PARAM_TOKEN, token);
      xml = xml.replace(PARAM_THUMBPRINT, thumbprint);
      xml = xml.replace(PARAM_OTP, otp);

      xml = xml.replace(PARAM_DOC_ID, op.getExtRefID());
      xml = xml.replace(PARAM_PAYEE_NAME, op.getCreditorName());
      xml = xml.replace(PARAM_CREDIT_IBAN, op.getCreditorAccount().getIban().getIban());
      xml = xml.replace(PARAM_DEBIT_IBAN, op.getDebtorAccount().getIban().getIban());
      xml = xml.replace(PARAM_CURRENCY, op.getInstructedAmount().getCurrency());
      xml = xml.replace(PARAM_AMOUNT, op.getInstructedAmount().getAmount().toString());
      xml = xml.replace(PARAM_REASON, description);

      //System.out.println(xml);

      answer = Util.doPostSync(URL_PAYMENT_OTP, xml, "application/xml");

      System.out.println("====================================================");
      System.out.println(answer);
      System.out.println("====================================================");
      LogManager.trace(getClass(), "makeTransaction() answer from IB -> " + answer);
    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal Error!");
    }

    if (answer.contains("<ERRORS>")) {
      throw new ApplicationException(ApplicationException.PSU_CREDENTIALS_INVALID, "Not valid OTP!");
    } else {
      try {
        refID = XmlParserMBIB.parseRef(answer);
      } catch (Exception e) {
        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal Error!");
      }

      if (refID == null || !refID.equalsIgnoreCase(op.getExtRefID())) {
        LogManager.trace(getClass(), "WARNING! - Ext. Refs doesnt match! " + op.getExtRefID() + " <> " + refID);
      }
    }

    return refID;
  }



  public ArrayList<CoreSystemAccountInfo> getAccounts(UserInfo userInfo) throws ApplicationException {
    LogManager.trace(getClass(), "getAccounts()", userInfo.toString());

    try {
      ArrayList<CoreSystemAccountInfo> list = getAccountsFromIB();

      return list;
    } catch (ApplicationException e) {
      LogManager.log(getClass(), e);

      throw e;
    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }
  }
  public CoreSystemAccountInfo getAccountDetails(String iban) throws ApplicationException {
    LogManager.trace(getClass(), "getAccountDetails()", iban);

    MbCoreSystemAccountInfo accountInfo;
    String thumbprint = UserFilter.getEIDASInfo().getThumbprint();

    String xml = ACCOUNT_DETAILS_XML.replace(PARAM_THUMBPRINT, thumbprint);
    xml = xml.replace(PARAM_ACCOUNT_ID, iban);

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(MB_process);
      ocs.registerOutParameter(2, OracleTypes.CLOB);

      ocs.setString(1, xml);

      ocs.execute();

      String xmlResult = ocs.getString(2);

      if (xmlResult.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.TOKEN_INVALID, "Not valid iban!");
      } else {
        accountInfo = XmlParserMBIB.parseAccount(xmlResult);
      }

      return accountInfo;


    } catch (Exception e) {
      LogManager.log(MBIBCommunicator.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(MBIBCommunicator.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(MBIBCommunicator.class, ex);
        }
      }
    }
  }

  private static ArrayList<CoreSystemAccountInfo> getAccountsFromIB() throws Exception {
    String eidasThumbprint = UserFilter.getEIDASInfo().getThumbprint();
    String token = UserFilter.getUserInfo().getSessionID();

    String params = "Sess=" + token + "&" + "Thumb="+ eidasThumbprint;
    LogManager.trace(MBIBCommunicator.class, "getAccountsFromIB()", params);

    String answer = Util.doPostSync(MBIdentityManagementCommunicator.URL, params, "application/x-www-form-urlencoded");

    LogManager.trace(MBIBCommunicator.class, "getAccountsFromIB()", answer);

    ArrayList<CoreSystemAccountInfo> list = XmlParserMBIB.parseAccounts(answer);

    return list;
  }

  //  public CoreSystemAccountInfo getAccountDetails(String iban) throws ApplicationException {
//    Connection connection = null;
//
//    CallableStatement ocs = null;
//    ResultSet rs = null;
//
//    try {
//      ArrayList<CoreSystemAccountInfo> list = getAccountsFromIB();

////      for (CoreSystemAccountInfo info : list) {
////        if (info.getIban().equals(iban)) {
////          return info;
////        }
////      }
//    } catch (ApplicationException e) {
//      LogManager.log(getClass(), e);
//
//      throw e;
//    } catch (Exception e) {
//      LogManager.log(getClass(), e);
//
//      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
//    }
//
//    return null;
//  }

  public static void main(String[] args) {
    // session-ID = 30db3148-4da6-42d4-bb41-d6f48f46d384
    // accounts = BG58SOMB91301000000637,BG81SOMB91302000000674,BG15SOMB91301000000635,
    // BG90SOMB91301000000837,BG16SOMB91301000000908,BG74SOMB91301000000834,BG86SOMB91301000000909,
    // BG02SOMB91301000000093,BG04SOMB91302000000120,BG05SOMB91301000001009,BG05SOMB91304000000034,BG94SOMB91301000000668

//    String extRefID = "18807343";
//
//    Amount amount = new Amount("BGN", new BigDecimal("890.12"));
//    String creditorName = "Joro Buhalkata";
//
//    AccountDetails creditorAccount = new AccountDetails();
//    creditorAccount.setIban(new IBAN("BG20UBBS88881000597232"));
//    AccountDetails debtorAccount = new AccountDetails();
//    debtorAccount.setIban(new IBAN("BG58SOMB91301000000637"));
//
//    PSD2RequestCommonData commonData = new PSD2RequestCommonData("abc1234567", "pib", null, null, "12234234", "PSDBG-BNB-121086224");
//
//    BGNCreditTransferOp op = new BGNCreditTransferOp(-1, extRefID, new Date(), amount,
//            debtorAccount, creditorName, creditorAccount, null, null, null, null, commonData);
    try {
      MBIBCommunicator com = new MBIBCommunicator();
//      com.getAccountBalances("BG11SOMB91301000000998");
//      Date dateFrom = new SimpleDateFormat("dd/MM/yyyy").parse("04/05/2018");
//      Date dateTo = new SimpleDateFormat("dd/MM/yyyy").parse("22/04/2020");
//      com.readTransactionsList("BG58SOMB91301000000637",dateFrom, dateTo,"true");
//    com.readTransactionsDetails("18805186");
//    com.getTransactionStatus("18805186");
//    com.getAccountBalances("BG58SOMB91301000000637");
//    com.makeTransaction(op);
      com.getAccountDetails("BG65SOMB91301000000123");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
