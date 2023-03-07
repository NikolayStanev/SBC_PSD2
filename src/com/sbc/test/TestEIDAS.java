package com.sbc.test;

import com.sbc.common.util.Util;
import com.sbc.util.eidas.EIDASCheck;
import com.sbc.util.eidas.EIDASInfo;
import com.sbc.util.eidas.EIDASUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-6
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public class TestEIDAS {
  public static void testConnectionTo(String aURL) throws Exception {
    TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
              public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
              }
              public void checkClientTrusted(
                      java.security.cert.X509Certificate[] certs, String authType) {
              }
              public void checkServerTrusted(
                      java.security.cert.X509Certificate[] certs, String authType) {
              }
            }
    };


    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
            new javax.net.ssl.HostnameVerifier(){

              public boolean verify(String hostname,
                                    javax.net.ssl.SSLSession sslSession) {
                return hostname.equals("130.61.222.244");
              }
            });

    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    URL destinationURL = new URL(aURL);
    HttpsURLConnection conn = (HttpsURLConnection) destinationURL.openConnection();
    conn.connect();
    Certificate[] certs = conn.getServerCertificates();
    for (Certificate cert : certs) {
      System.out.println("Certificate is: " + cert);
      if(cert instanceof X509Certificate) {
        try {
          ( (X509Certificate) cert).checkValidity();
          System.out.println("Certificate is active for current date");
        } catch(CertificateExpiredException cee) {
          System.out.println("Certificate is expired");
        }
      }
    }
  }

  public static Certificate[] load(String path, String pwd) throws Exception {
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    KeyStore keystore = KeyStore.getInstance("PKCS12");


    keystore.load(new FileInputStream(path), pwd.toCharArray());
    //keystore.load(new FileInputStream(certificate), password);
    kmf.init(keystore, pwd.toCharArray());
    Enumeration<String> aliases = keystore.aliases();
    while (aliases.hasMoreElements()) {
      String alias = aliases.nextElement();
      //System.out.println(alias);
      if (keystore.getCertificate(alias).getType().equals("X.509")) {

        Certificate[] chain = keystore.getCertificateChain(alias);

        return chain;
      }
    }

    return null;
  }


  public static void show(String path, String pwd) throws Exception {

    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    KeyStore keystore = KeyStore.getInstance("PKCS12");


    keystore.load(new FileInputStream(path), pwd.toCharArray());
    //keystore.load(new FileInputStream(certificate), password);
    kmf.init(keystore, pwd.toCharArray());
    Enumeration<String> aliases = keystore.aliases();
    while (aliases.hasMoreElements()) {
      String alias = aliases.nextElement();
      //System.out.println(alias);
      if (keystore.getCertificate(alias).getType().equals("X.509")) {

        Certificate[] chain = keystore.getCertificateChain(alias);

        boolean validate = EIDASUtil.validateSignatures(chain);

        System.out.println("VALID: " + validate);



        X509Certificate root = (X509Certificate)chain[2];

        //System.out.println(root);

        //byte[] signature = root.getSignature();

        String hex = EIDASUtil.generateThumbprint(root);

        Principal issuer = root.getSubjectDN();
        String name = issuer.getName();
        //Util.bytesToHex(signature);
        System.out.println(name + " = " + hex);

        EIDASCheck.check(chain);

        //parseEIDAS((X509Certificate) chain[0]);

        //Date expDate = ((X509Certificate) keystore.getCertificate(alias)).getNotAfter();
        //Date fromDate= ((X509Certificate) keystore.getCertificate(alias)).getNotBefore();
        //System.out.println("Expiray Date:-"+expDate );
        //System.out.println("From Date:-"+fromDate);
      }
    }

  }

  public static void parseEIDAS(X509Certificate cert) throws Exception {
    //String pem = EIDASUtil.toPEM(cert);
    //System.out.println(pem);

    String organizationNumber = EIDASUtil.getOrganizationID(cert);
    System.out.println("ORG NUM = " + organizationNumber);

    String thumbprint = EIDASUtil.generateThumbprint(cert);
    System.out.println("THUMBPRINT = " + thumbprint.toUpperCase());

    //BDDC46B84F79B0F7DF4084824728460377F11170
    //BDDC46B84F79B0F7DF4084824728460377F11170

    String ncaName = null;
    String ncaID = null;
    HashSet<String> rolesSet = new HashSet<>();

    long millis0 = System.currentTimeMillis();

    byte[] encodedExtensionValue = cert.getExtensionValue("1.3.6.1.5.5.7.1.3");

    ASN1Sequence primitive = (ASN1Sequence)JcaX509ExtensionUtils.parseExtensionValue(encodedExtensionValue);


    ASN1Encodable[] items = primitive.toArray();
    for (ASN1Encodable item : items) {
      //System.out.println(item.getClass() + " -> " + item);
      if (item instanceof ASN1Sequence) {
        ASN1Sequence sequence = ((ASN1Sequence) item);
        ASN1Encodable[] items2 = sequence.toArray();

        if (items2[0] instanceof ASN1ObjectIdentifier) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)items2[0];

          if (oid.getId().equalsIgnoreCase("0.4.0.19495.2"))  {
            ASN1Sequence data = (ASN1Sequence)items2[1];

            ASN1Encodable[] elements = data.toArray();

            ASN1Sequence rolesSeq = (ASN1Sequence)elements[0];
            ASN1Encodable[] roles = rolesSeq.toArray();

            for (ASN1Encodable role : roles) {
              ASN1Sequence rolePair = (ASN1Sequence)role;
              ASN1Encodable[] rolePairArray = rolePair.toArray();

              rolesSet.add(rolePairArray[1].toString());
            }

            ncaName = elements[1].toString();
            ncaID = elements[2].toString();


            System.out.println(data);
          }
        }
      }


    }




    long millis = System.currentTimeMillis() - millis0;
    System.out.println("OK " + millis);
    //HashMap<String , String> map = EIDASUtil.toMap(cert, "1.3.6.1.5.5.7.1.3");

    //System.out.println(map);

    //0.4.0.19495.2=[[[0.4.0.19495.1.1, PSP_AS]], Bulgarian National Bank, BG-BNB]
  }

  public static void main(String[] args) {

    String path0 = "C:\\Users\\pavel.bonev\\Desktop\\SBC UBXPSD2\\MB_PSD2_Sandbox\\api.municipalbank.bg-PSP_AS.pfx";
    String pwd0 = "5625446";

    String path = "C:\\Users\\pavel.bonev\\Desktop\\SBC UBXPSD2\\MB\\MB CERTS\\api.municipalbank.bg-PSP_AI+PSP_PI+PSP_IC.pfx";
    String pwd = "5621818";
    try {
      Certificate[] certs = load(path, pwd);

      //parseEIDAS((X509Certificate)certs[0]);

      long millis = System.currentTimeMillis();
      EIDASInfo info = EIDASInfo.build((X509Certificate)certs[0]);
      System.out.println(info.toString());
//      for (int i=0;i<1000;i++) {
//        EIDASInfo info = EIDASCheck.check(certs);
//      }

      System.out.println(System.currentTimeMillis()- millis);
      //System.out.println(info);


      //show(path, pwd);

//      InputStream input = new FileInputStream("ca2.txt");
//
//      Properties prop = new Properties();
//
//      prop.load(input);
//
//      System.out.println(prop.toString());



      //testConnectionTo("https://130.61.222.244");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
