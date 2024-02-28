package com.sbc.test.hub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbc.psd2.controller.impl.mb.MBIBCommunicator;
import com.sbc.psd2.controller.impl.mb.OTPSSCommunicator;
import com.sbc.psd2.data.PSD2RequestCommonData;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.rest.*;
import com.sbc.psd2.controller.UserFilter;
import com.sbc.psd2.rest.util.Util;
import com.sbc.util.eidas.EIDASInfo;
import com.sbc.util.eidas.EIDASUtil;

import javax.net.ssl.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-4-9
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class TestFullCircle {

  static {
    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
              public java.security.cert.X509Certificate[] getAcceptedIssuers() {

                return null;
              }
              public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
              }

              public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                if (!EIDASUtil.validateSignatures(certs)) {
                  throw new SecurityException("Bad certs chain!");
                }

                System.out.println("Here!");
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
  }

//  {"access": {"balances":[{"iban":"BG12SOMB91301000000010", "currency":"BGN"}],
//    "transactions":[{"iban": "BG12SOMB91302000000073", "currency":"BGN"}],
//    "accounts":[{"iban": "BG17SOMB91302000000080", "currency":"BGN"}]},
//    "validUntil": "2021-01-01",
//          "frequencyPerDay" : "4"
//  }
  private static final String HUB_UID = "Niki_137527";
  private static final String HUB_PWD = "123qwer";

  private static final String APP_ID = "A29DE139B80FC30106334F0ED46096E14067D8A5";
  private static final String APP_SECRET = "5F5F132E8E3B9BBA3C96C9FC87D5ADAE";

  private static final String EIDAS_THUMBRINT = "BDDC46B84F79B0F7DF4084824728460377F11170";
  private static final String EIDAS_TPP_ID = "PSDBG-BNB-121086224";

  //private static final String LOGIN_URL = "http://172.16.51.121:8080/THE_HUB/Login";
  private static final String LOGIN_URL = "https://sandbox.municipalbank.bg/THE_HUB/Login";

  private static final String LOGIN_XML = "<?xml+version=\"1.0\"?><LOGIN><APISECRET>[API_SECRET]</APISECRET>" +
          "<APIID>[APIID]</APIID>" +
          "<USER_NAME>[UID]</USER_NAME>" +
          "<USER_PASS>[PWD]</USER_PASS>" +
          "<TOKEN_OTPSS>[OTP]</TOKEN_OTPSS>" +
          "<USER_LANG>BG</USER_LANG>" +
          "<FUNC_ID></FUNC_ID></LOGIN>";

  public static HUBLoginAnswer loginIntoHUB(String appID, String appSecret, String uid, String pwd, String otp) throws Exception {
    String textXML = LOGIN_XML.replace("[APIID]", appID);
    textXML = textXML.replace("[UID]", uid);
    textXML = textXML.replace("[PWD]", pwd);
    textXML = textXML.replace("[OTP]", otp);
    textXML = textXML.replace("[API_SECRET]", appSecret);

    String params = "textXML=" + URLEncoder.encode(textXML, "UTF-8");
    //System.out.println(params);

    String answer = Util.doPostSync(LOGIN_URL, params, "application/x-www-form-urlencoded");

    System.out.println(answer);

    HUBLoginAnswer hubLoginAnswer = HUBLoginAnswer.build(answer);

    return hubLoginAnswer;
  }


  private static void setFinalStatic(Field field, Object newValue) throws Exception {
    field.setAccessible(true);

    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

    InheritableThreadLocal t = (InheritableThreadLocal) field.get(null);
    t.set(newValue);
  }

  private static BGNCreditTransferOp genTransferOp(String creditIBAN, String debitIBAN, String currency, BigDecimal amountDec, String payeeName) throws Exception {
    Amount amount = new Amount(currency, amountDec);

    AccountDetails creditorAccount = new AccountDetails();
    creditorAccount.setIban(creditIBAN);
    AccountDetails debtorAccount = new AccountDetails();
    debtorAccount.setIban(debitIBAN);

    PSD2RequestCommonData commonData = new PSD2RequestCommonData("abc1234567", "pib", null, null, "12234234", EIDAS_TPP_ID);

    BGNCreditTransferOp op = new BGNCreditTransferOp(-1, null, new Date(), amount,
            debtorAccount, payeeName, creditorAccount, null, null, null, null,null,null,null, commonData);

    return op;
  }


  public static void testPayment() throws Exception {
    System.out.println(">Enter Login OTP:");
    Scanner scanner = new Scanner(System.in);
    String otp = scanner.nextLine();

    HUBLoginAnswer hubLoginAnswer = loginIntoHUB(APP_ID, APP_SECRET, HUB_UID, HUB_PWD, otp.trim());

    System.out.println(">Got sessionID = " + hubLoginAnswer.getSessionID());

    EIDASInfo eidasINFO = new EIDASInfo(EIDAS_TPP_ID, null, null, null, EIDAS_THUMBRINT);
    UserInfo userInfo = new UserInfo(HUB_UID, null, HUB_UID, hubLoginAnswer.getSessionID());

    setFinalStatic(UserFilter.class.getDeclaredField("threadLocalEIDASInfo"), eidasINFO);
    setFinalStatic(UserFilter.class.getDeclaredField("threadLocalUser"), userInfo);

    int ibanIndex = 12;
    String debitIBAN = hubLoginAnswer.getIbans().get(ibanIndex);
    System.out.println(debitIBAN);

    BGNCreditTransferOp op = genTransferOp("BG20UBBS88881000597232", debitIBAN, "BGN", new BigDecimal("2.91"), "Жоро Бицепса");
    OTPSSCommunicator otpssCommunicator = new OTPSSCommunicator();

    otpssCommunicator.generateOTP(op);

    System.out.println("> Got refID = " + op.getExtRefID());

    System.out.println(">Enter Sign OTP:");
    otp = scanner.nextLine();

    userInfo.setOtp(otp);

    MBIBCommunicator com = new MBIBCommunicator();
    String str = com.makeTransaction(op);

    System.out.println(str);
  }

  public static void testLogin() throws Exception {
    System.out.println(">Enter Login OTP:");
    Scanner scanner = new Scanner(System.in);
    String otp = scanner.nextLine();

    HUBLoginAnswer hubLoginAnswer = loginIntoHUB(APP_ID, APP_SECRET, HUB_UID, HUB_PWD, otp.trim());

    System.out.println(">Got sessionID = " + hubLoginAnswer.getSessionID());
  }


  public static void testTransactions() throws Exception {
    System.out.println(">Enter Login OTP:");
    Scanner scanner = new Scanner(System.in);
    String otp = scanner.nextLine();

//    HUBLoginAnswer hubLoginAnswer = loginIntoHUB(APP_ID, APP_SECRET, HUB_UID, HUB_PWD, otp.trim());
//
//    System.out.println(">Got sessionID = " + hubLoginAnswer.getSessionID());

    EIDASInfo eidasINFO = new EIDASInfo(EIDAS_TPP_ID, null, null, null, EIDAS_THUMBRINT);
    //UserInfo userInfo = new UserInfo(HUB_UID, null, HUB_UID, hubLoginAnswer.getSessionID());

    setFinalStatic(UserFilter.class.getDeclaredField("threadLocalEIDASInfo"), eidasINFO);
    //setFinalStatic(UserFilter.class.getDeclaredField("threadLocalUser"), userInfo);

    MBIBCommunicator com = new MBIBCommunicator();
    Date dateFrom = new SimpleDateFormat("dd/MM/yyyy").parse("01/04/2018");
    Date dateTo = new SimpleDateFormat("dd/MM/yyyy").parse("20/05/2020");
    ReadTransactionsListResponse response = com.readTransactionsList("BG58SOMB91301000000637", dateFrom, dateTo, "true");

    System.out.println("> Got response = " + response.getTransactions().getBooked().size());

    ObjectMapper mapper = new ObjectMapper();
    System.out.println(mapper.writeValueAsString(response));

    System.out.println(mapper.writeValueAsString(response.getTransactions().getBooked().get(0)));


    Transactions trans = com.readTransactionsDetails(response.getTransactions().getBooked().get(0).getTransactionId());

    System.out.println(trans);
    System.out.println(mapper.writeValueAsString(trans));
  }


  public static void main(String[] args) {
    try {
      testLogin();
      //testPayment();
      //testTransactions();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
