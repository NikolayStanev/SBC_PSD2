package com.sbc.psd2.controller.impl.mb;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.impl.mb.util.XmlParserMBIB;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.controller.SCACommunicator;
import com.sbc.psd2.data.PSD2RequestCommonData;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.creditTransfer.dao.BGNCreditTransferOpDAO;
import com.sbc.psd2.data.rest.AccountDetails;
import com.sbc.psd2.data.rest.Amount;
import com.sbc.psd2.data.rest.IBAN;
import com.sbc.psd2.controller.UserFilter;
import com.sbc.psd2.rest.util.Util;

import javax.net.ssl.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public class OTPSSCommunicator implements SCACommunicator {


  private static final String URL_CONSENT_OTP;
  private static final String URL_PAYMENT_OTP;

  private static final String PARAM_TOKEN = "[TOKEN]";
  private static final String PARAM_THUMBPRINT = "[THUMBPRINT]";
  private static final String PARAM_TEXT_FOR_SIGNING = "[TEXT_FOR_SIGNING]";
  private static final String PARAM_OTP = "[OTP]";

  private static final String PARAM_PAYEE_NAME = "[PAYEE_NAME]";
  private static final String PARAM_CREDIT_IBAN = "[CREDIT_IBAN]";
  private static final String PARAM_DEBIT_IBAN = "[DEBIT_IBAN]";
  private static final String PARAM_CURRENCY = "[CURRENCY]";
  private static final String PARAM_AMOUNT = "[AMOUNT]";
  private static final String PARAM_REASON = "[REASON]";


  static {
    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
              public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
              }
              public void checkClientTrusted(
                      java.security.cert.X509Certificate[] certs, String authType) {
              }
              public void checkServerTrusted(
                      java.security.cert.X509Certificate[] certs, String authType) {
              }
            }
    };

// Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      e.printStackTrace();
    }

    HostnameVerifier allHostsValid = new HostnameVerifier() {
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    };

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    String endPoint = AppConfig.getInstance().getScaCommunicatorEndPoint();


    URL_CONSENT_OTP = endPoint + "/THE_HUB/Enquiry";
    URL_PAYMENT_OTP = endPoint + "/THE_HUB/Package";
  }

  private static final String CONSENT_OTP_PUSH_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ENQUIRY sessid=\"[TOKEN]\" langid=\"BG\" thumb=\"[THUMBPRINT]\">" +
          "  <OTPPUSH phrase=\"[TEXT_FOR_SIGNING]\"/>" +
          "</ENQUIRY>";

  private static final String CONSENT_OTP_CHECK_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ENQUIRY sessid=\"[TOKEN]\" langid=\"BG\" thumb=\"[THUMBPRINT]\">" +
          "  <OTPCHECK phrase=\"[TEXT_FOR_SIGNING]\" otp=\"[OTP]\"/>" +
          "</ENQUIRY>";


  private static final String PAYMENT_OTP_PUSH_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
          "<PACKAGE sessid=\"[TOKEN]\" langid=\"BG\" action=\"confirm\" thumb=\"[THUMBPRINT]\" id=\"p1\" massPay=\"false\">\n" +
          "<DOCUMENTS>" +
          "<DOCUMENT " +
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

  public void generateOTP(ConsentOp op) throws ApplicationException {
    LogManager.trace(getClass(), "generateOTP()", op.toString());

    try {
      // todo: remove hack after testing
      //String thumbprint = "BDDC46B84F79B0F7DF4084824728460377F11170";
      //String token = "8552c782-264e-4a3e-bf34-a09a79be07fb";
      // todo: end of hack

      String thumbprint = UserFilter.getEIDASInfo().getThumbprint();
      String token = UserFilter.getUserInfo().getSessionID();
      String textForSigning = "Please authorize consent: " + Util.genTextForSigning(op);

      String xml = CONSENT_OTP_PUSH_XML.replace(PARAM_TOKEN, token);
      xml = xml.replace(PARAM_THUMBPRINT, thumbprint);
      xml = xml.replace(PARAM_TEXT_FOR_SIGNING, textForSigning);

      String answer = Util.doPostSync(URL_CONSENT_OTP, xml, "application/xml");
      LogManager.trace(getClass(), "generateOTP() -> " + answer);

      if (answer.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.TOKEN_INVALID, "Not valid token!");
      }

    } catch (ApplicationException e) {
      LogManager.log(getClass(), e);

      throw e;
    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }
  }

  public boolean checkOTP(ConsentOp op, String otp) throws ApplicationException {
    LogManager.trace(getClass(), "checkOTP()", otp,  op.toString());

    try {
      // todo: remove hack for testing
      //String thumbprint = "BDDC46B84F79B0F7DF4084824728460377F11170";
      //String token = "8552c782-264e-4a3e-bf34-a09a79be07fb";
      // todo: end of hack

      String thumbprint = UserFilter.getEIDASInfo().getThumbprint();
      String token = UserFilter.getUserInfo().getSessionID();
      String textForSigning = "Please authorize consent: " + Util.genTextForSigning(op);

      String xml = CONSENT_OTP_CHECK_XML.replace(PARAM_TOKEN, token);
      xml = xml.replace(PARAM_THUMBPRINT, thumbprint);
      xml = xml.replace(PARAM_TEXT_FOR_SIGNING, textForSigning);
      xml = xml.replace(PARAM_OTP, otp);

      String answer = Util.doPostSync(URL_CONSENT_OTP, xml, "application/xml");
      LogManager.trace(getClass(), "checkOTP() -> " + answer);

      if (answer.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.PSU_CREDENTIALS_INVALID, "Not valid otp!");
      }

    } catch (ApplicationException e) {
      LogManager.log(getClass(), e);

      throw e;
    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }

    return true;
  }

  public void generateOTP(BGNCreditTransferOp op) throws ApplicationException {
    LogManager.trace(getClass(), "generateOTP()", op.toString());

    try {
      // todo: remove hack after testing
      //String thumbprint = "BDDC46B84F79B0F7DF4084824728460377F11170";
      //String token = "d411f530-a502-48a3-a7b4-c614e91d5073";
      //String tppID = "PSDBG-BNB-121086224";
      // todo: end of hack

      String thumbprint = UserFilter.getEIDASInfo().getThumbprint();
      String token = UserFilter.getUserInfo().getSessionID();
      String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();

      String description = "PAYMENT TPP " + tppID;

      String xml = PAYMENT_OTP_PUSH_XML.replace(PARAM_TOKEN, token);
      xml = xml.replace(PARAM_THUMBPRINT, thumbprint);

      xml = xml.replace(PARAM_PAYEE_NAME, op.getCreditorName());
      xml = xml.replace(PARAM_CREDIT_IBAN, op.getCreditorAccount().getIban().getIban());
      xml = xml.replace(PARAM_DEBIT_IBAN, op.getDebtorAccount().getIban().getIban());
      xml = xml.replace(PARAM_CURRENCY, op.getInstructedAmount().getCurrency());
      xml = xml.replace(PARAM_AMOUNT, op.getInstructedAmount().getAmount().toString());
      xml = xml.replace(PARAM_REASON, description);

      //System.out.println(xml);

      String answer = Util.doPostSync(URL_PAYMENT_OTP, xml, "application/xml");

      //System.out.println("====================================================");
      System.out.println(answer);
      //System.out.println("====================================================");
      LogManager.trace(getClass(), "generateOTP() -> " + answer);

      if (answer.contains("<ERRORS>")) {
        throw new ApplicationException(ApplicationException.TOKEN_INVALID, "Not valid token!");
      } else {
        String refID = XmlParserMBIB.parseRef(answer);

        op.setExtRefID(refID);

        BGNCreditTransferOpDAO.update(op);
      }

    } catch (ApplicationException e) {
      LogManager.log(getClass(), e);

      throw e;
    } catch (Exception e) {
      LogManager.log(getClass(), e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }
  }

  public boolean checkOTP(BGNCreditTransferOp op, String otp) throws ApplicationException {
    // NOT IMPLEMENTED!!!

    return true;
  }




  public static void main(String[] args) {
    String otp = "107805";

    Date now = new Date();

    HashMap<String, Account> accountMap = new HashMap<>();
    Account acc1 = new Account(1, "BG1234AU11111", "XYZ", true, true, true, 1, "BGN", 4, now, null);
    Account acc2 = new Account(1, "BG1234AU11118", "XYZ", true, true, true, 1, "EUR", 4, now, null);

    accountMap.put("BG1234AU11111", acc1);
    accountMap.put("BG1234AU11118", acc2);

    ConsentOp consent = new ConsentOp(1, accountMap, true, now, 4, "APPROVED", "GHJ-UUU-72172", null, true, now);


    // session-ID = 30db3148-4da6-42d4-bb41-d6f48f46d384
    // accounts = BG58SOMB91301000000637,BG81SOMB91302000000674,BG15SOMB91301000000635,
    // BG90SOMB91301000000837,BG16SOMB91301000000908,BG74SOMB91301000000834,BG86SOMB91301000000909,
    // BG02SOMB91301000000093,BG04SOMB91302000000120,BG05SOMB91301000001009,BG05SOMB91304000000034,BG94SOMB91301000000668

    Amount amount = new Amount("BGN", new BigDecimal("890.12"));
    String creditorName = "Joro Buhalkata";

    AccountDetails creditorAccount = new AccountDetails();
    creditorAccount.setIban(new IBAN("BG20UBBS88881000597232"));
    AccountDetails debtorAccount = new AccountDetails();
    debtorAccount.setIban(new IBAN("BG58SOMB91301000000637"));

    PSD2RequestCommonData commonData = new PSD2RequestCommonData("abc1234567", "pib", null, null, "12234234", "PSDBG-BNB-121086224");

    BGNCreditTransferOp op = new BGNCreditTransferOp(-1, null, new Date(), amount,
            debtorAccount, creditorName, creditorAccount, null, null, null, null,null,null,null, commonData);
    try {
      OTPSSCommunicator com = new OTPSSCommunicator();

      //com.generateOTP(consent);
      com.generateOTP(op);

      //com.checkOTP(consent, otp);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
