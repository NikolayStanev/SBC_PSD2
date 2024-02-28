package com.sbc.test;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.rest.util.Util;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-9-18
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class TestOTPSS {
  private static final char DELIMITER = '#';

  private static final String USER_ID = "sbc";
  private static final String PWD = "pass1234";

  private static final String OTPSS_URL = "https://172.16.51.97/OTPSS_10n/";//"https://172.16.51.97/OTPSS/Sign"; //"http://172.16.51.152:8080/OTPSS/Sign";//https://172.16.51.97/OTPSS/";


  private static final String XML_SIGN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "<SIGN username=\"[UID]\" password=\"[PWD]\">\n" +
          "   <GETSIGN tokenid=\"[TOKEN_ID]\" phrase=\"[TEXT_FOR_SIGNING]\" description=\"[DESCRIPTION]\" referenceid=\"[REF_ID]\" />\n" +
          "</SIGN>";


  private static final String XML_CHECK = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "<SIGN username=\"[UID]\" password=\"[PWD]\">\n" +
          "   <VALIDATESIGN tokenid=\"[TOKEN_ID]\" phrase=\"[TEXT_FOR_SIGNING]\" code=\"[OTP]\" />\n" +
          "</SIGN>";          // 365605

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

  }

  private static String doPostSync(final String urlToRead, final String content) throws IOException {
    final String charset = "UTF-8";
    // Create the connection
    HttpURLConnection connection = (HttpURLConnection) new URL(urlToRead).openConnection();
    // setDoOutput(true) implicitly set's the request type to POST
    connection.setDoOutput(true);
    connection.setRequestProperty("Accept-Charset", charset);
    //connection.setRequestProperty("Content-type", "application/json");
    connection.setRequestProperty("Content-Type", "text/xml");

    // Write to the connection
    OutputStream output = connection.getOutputStream();
    output.write(content.getBytes(charset));
    output.close();

    // Check the error stream first, if this is null then there have been no issues with the request
    InputStream inputStream = connection.getErrorStream();
    if (inputStream == null)
      inputStream = connection.getInputStream();

    // Read everything from our stream
    BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream, charset));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = responseReader.readLine()) != null) {
      response.append(inputLine);
    }
    responseReader.close();

    return response.toString();
  }

  public static void pushOTP(ConsentOp op, String token) throws ApplicationException {
    try {
      StringBuilder sb = new StringBuilder();
      for (String iban : op.getAccountMap().keySet()) {
        sb.append(iban);
        sb.append(',');
      }

      String accounts = sb.toString();
      if (accounts.length() > 0) {
        accounts = accounts.substring(0, accounts.length() - 1);
      }

      String textForSigning = op.getConsentId() + DELIMITER + accounts;
      String description = "Please authorize access to these accounts: " + accounts;

      String xmlSign = XML_SIGN.replace("[UID]", USER_ID)
              .replace("[PWD]", PWD)
              .replace("[TOKEN_ID]", token)
              .replace("[TEXT_FOR_SIGNING]", textForSigning)
              .replace("[DESCRIPTION]", description)
              .replace("[REF_ID]", op.getConsentId());


      System.out.println( xmlSign);
      String xmlResponse = doPostSync(OTPSS_URL, xmlSign);

      System.out.println(xmlResponse);
    } catch (Exception e) {
      e.printStackTrace();

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Problem with OTPSS!");
    }
  }


  public static void pushOTP(BGNCreditTransferOp op, String token) throws ApplicationException {
    try {
      String fromIBAN = op.getDebtorAccount().getIban();
      String toIBAN = op.getCreditorAccount().getIban();
      String sum = op.getInstructedAmount().getAmount().toString();
      String currency = op.getInstructedAmount().getCurrency();
      String creditorName = op.getCreditorName();

      String textForSigning = op.getPaymentId() + DELIMITER + fromIBAN + DELIMITER +
              toIBAN + DELIMITER + sum + DELIMITER + currency;

      String description = "Please authorize payment from "+ fromIBAN+ " to " + creditorName + ", IBAN:" + toIBAN + " of " +  sum + " " + currency;

      System.out.println(description);

      String xmlSign = XML_SIGN.replace("[UID]", USER_ID)
              .replace("[PWD]", PWD)
              .replace("[TOKEN_ID]", token)
              .replace("[TEXT_FOR_SIGNING]", textForSigning)
              .replace("[DESCRIPTION]", description)
              .replace("[REF_ID]", op.getPaymentId());


      System.out.println(xmlSign);
      String xmlResponse = doPostSync(OTPSS_URL, xmlSign);

      System.out.println(xmlResponse);
    } catch (Exception e) {
      e.printStackTrace();

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Problem with OTPSS!");
    }
  }


  public static void checkOTP(BGNCreditTransferOp op, String token, String otp) throws ApplicationException {
    try {
      String fromIBAN = op.getDebtorAccount().getIban();
      String toIBAN = op.getCreditorAccount().getIban();
      String sum = op.getInstructedAmount().getAmount().toString();
      String currency = op.getInstructedAmount().getCurrency();

      String textForSigning = op.getPaymentId() + DELIMITER + fromIBAN + DELIMITER +
              toIBAN + DELIMITER + sum + DELIMITER + currency;


      String xmlSign = XML_CHECK.replace("[UID]", USER_ID)
              .replace("[PWD]", PWD)
              .replace("[TOKEN_ID]", token)
              .replace("[TEXT_FOR_SIGNING]", textForSigning)
              .replace("[OTP]", otp);


      System.out.println(xmlSign);
      String xmlResponse = doPostSync(OTPSS_URL, xmlSign);

      System.out.println(xmlResponse);
    } catch (Exception e) {
      e.printStackTrace();

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Problem with OTPSS!");
    }
  }

  public static void login(String uid, String pwd, String appID, String appSecret, String otp)  {
    //apisecret=5F5F132E8E3B9BBA3C96C9FC87D5ADAE
    //apiid=A29DE139B80FC30106334F0ED46096E14067D8A5
    //usr : rafraf
    //pwd: 123qwe
    // http://172.16.52.47:8080/THE_HUB/Login

    //S_USERID=rafraf&password=123qwe&token_otpss=&S_LANG=BG&textXML=%3C%3Fxml+version%3D%221.0%22%3F%3E%3CLOGIN%3E%3CAPISECRET%3E5F5F132E8E3B9BBA3C96C9FC87D5ADAE%3C%2FAPISECRET%3E%3CAPIID%3EA29DE139B80FC30106334F0ED46096E14067D8A5%3C%2FAPIID%3E%3CUSER_NAME%3Erafraf%3C%2FUSER_NAME%3E%3CUSER_PASS%3E123qwe%3C%2FUSER_PASS%3E%3CTOKEN_OTPSS%3E%3C%2FTOKEN_OTPSS%3E%3CUSER_LANG%3EBG%3C%2FUSER_LANG%3E%3CFUNC_ID%3E%3C%2FFUNC_ID%3E%3C%2FLOGIN%3E&apiid=A29DE139B80FC30106334F0ED46096E14067D8A5&apisecret=5F5F132E8E3B9BBA3C96C9FC87D5ADAE

    String URL = "http://172.16.52.47:8080/THE_HUB/Login";

    String textXMLPattern = "<?xml+version=\"1.0\"?><LOGIN><APISECRET>[API_SECRET]</APISECRET>"+
            "<APIID>[APIID]</APIID>"+
            "<USER_NAME>[UID]</USER_NAME>"+
            "<USER_PASS>[PWD]</USER_PASS>"+
            "<TOKEN_OTPSS>[OTP]</TOKEN_OTPSS>"+
            "<USER_LANG>BG</USER_LANG>"+
            "<FUNC_ID></FUNC_ID></LOGIN>";

    String textXML = textXMLPattern.replace("[APIID]", appID);
    textXML = textXML.replace("[UID]", uid);
    textXML = textXML.replace("[PWD]", pwd);
    textXML = textXML.replace("[OTP]", otp);
    textXML = textXML.replace("[API_SECRET]", appSecret);


    try {
      String params = "textXML="+ URLEncoder.encode(textXML, "UTF-8");

      System.out.println(params);

      String answer = Util.doPostSync(URL, params, "application/x-www-form-urlencoded");

      System.out.println(answer);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public static void main(String args[]) {
    try {

      int id = 25;
      String tokenID = "187";
      String otp = "168411";
      //ConsentOp op = ConsentOpDAO.getConsentByID(id);
      //TestOTPSS.pushOTP(op, token);

      //String paymentID = "065d653e-b53c-4918-aecc-59f569870d70";
      //BGNCreditTransferOp op = BGNCreditTransferOpDAO.getOpByPaymentID(paymentID, "test");

      //TestOTPSS.pushOTP(op, tokenID);

      //TestOTPSS.checkOTP(op, tokenID, otp);

      String uid = "nista"; // String uid = "pavelbo";
      String pwd = "123qwe";
      String appID = "A29DE139B80FC30106334F0ED46096E14067D8A5";
      String appSecret = "5F5F132E8E3B9BBA3C96C9FC87D5ADAE";
      String otp2 = "239234";      // seal uid pwd - pavelbo/1234abcd



      // session-ID = 30db3148-4da6-42d4-bb41-d6f48f46d384
      // accounts = BG58SOMB91301000000637,BG81SOMB91302000000674,BG15SOMB91301000000635,
      // BG90SOMB91301000000837,BG16SOMB91301000000908,BG74SOMB91301000000834,BG86SOMB91301000000909,
      // BG02SOMB91301000000093,BG04SOMB91302000000120,BG05SOMB91301000001009,BG05SOMB91304000000034,BG94SOMB91301000000668

      TestOTPSS.login(uid, pwd, appID, appSecret, otp2);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
