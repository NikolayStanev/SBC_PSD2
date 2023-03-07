package com.sbc.util.eidas;

import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Util;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-6
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class EIDASUtil {
  private static final Properties caFingerprints;

  static {
    caFingerprints = new Properties();

    try {
      InputStream input = new FileInputStream("ca2.txt");

      caFingerprints.load(input);
    } catch (Exception e) {

      caFingerprints.put("CN=B-Trust Root Advanced CA, OU=B-Trust, O=BORICA AD, OID.2.5.4.97=NTRBG-201230426, C=BG", "ba11d6ad94b24fc916113af682cd762ab3bfd775");

      e.printStackTrace();
    }


  }

  public static String getThumbprint(String name) {
    return (String)caFingerprints.get(name);
  }

  public static HashSet<String> getCrlDistributionPoint(X509Certificate certificate) throws Exception {
    HashSet<String> list = new HashSet<>();

    byte[] crlDistributionPointDerEncodedArray = certificate.getExtensionValue(Extension.cRLDistributionPoints.getId());

    ASN1InputStream oAsnInStream = new ASN1InputStream(new ByteArrayInputStream(crlDistributionPointDerEncodedArray));
    ASN1Primitive derObjCrlDP = oAsnInStream.readObject();
    DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;

    oAsnInStream.close();

    byte[] crldpExtOctets = dosCrlDP.getOctets();
    ASN1InputStream oAsnInStream2 = new ASN1InputStream(new ByteArrayInputStream(crldpExtOctets));
    ASN1Primitive derObj2 = oAsnInStream2.readObject();
    CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);

    oAsnInStream2.close();

    for (DistributionPoint dp : distPoint.getDistributionPoints()) {
      DistributionPointName dpn = dp.getDistributionPoint();
      // Look for URIs in fullName
      if (dpn != null) {
        if (dpn.getType() == DistributionPointName.FULL_NAME) {
          GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
          // Look for an URI
          for (int j = 0; j < genNames.length; j++) {
            if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
              String url = DERIA5String.getInstance(genNames[j].getName()).getString();
              list.add(url);
            }
          }
        }
      }
    }

//    for (String url : list) {
//      System.out.println(url);
//    }

    return list;
  }


//  public static String getExtensionValue(X509Certificate cert, String oid) throws IOException {
//    String decoded = null;
//    byte[] extensionValue = cert.getExtensionValue(oid);
//
//    if (extensionValue != null) {
//      ASN1Primitive derObject = toDERObject(extensionValue);
//      if (derObject instanceof DEROctetString) {
//        DEROctetString derOctetString = (DEROctetString) derObject;
//
//        derObject = toDERObject(derOctetString.getOctets());
//        if (derObject instanceof ASN1String) {
//          ASN1String s = (ASN1String) derObject;
//          decoded = s.getString();
//        }
//
//      }
//    }
//    return decoded;
//  }



  public static HashMap<String, String> toMap(X509Certificate certificate, String oid) throws IOException {
    HashMap<String, String> map = new HashMap<>();

    byte[] encodedExtensionValue = certificate.getExtensionValue(oid);
    if (encodedExtensionValue != null) {
      ASN1Primitive primitive = JcaX509ExtensionUtils.parseExtensionValue(encodedExtensionValue);
      //System.out.println(oid + " = " + primitive.toString());
      map.put(oid, primitive.toString());


      toMap(map, primitive);
      //System.out.println();
    }

    return map;
  }

  public static HashSet<X509CRL> toCRL(Set<String> crlURLs) throws Exception {
    HashSet<X509CRL> crlList = new HashSet<>();

    CertificateFactory cf = CertificateFactory.getInstance("X509");

    for (String crlURL : crlURLs) {
      URL url = new URL(crlURL);
      URLConnection connection = url.openConnection();

      X509CRL crl = null;
      try (DataInputStream inStream = new DataInputStream(connection.getInputStream())) {

        crl = (X509CRL) cf.generateCRL(inStream);

        crlList.add(crl);
      }

    }

    return crlList;
  }

  public static boolean isSelfSigned(X509Certificate cert) {
    if (cert.getIssuerDN().equals(cert.getSubjectDN()))  {
       return true;
    }

    return false;
  }

  public static boolean validateSignatures(Certificate[] chain) {
    if (chain == null || chain.length == 0) {
      return false;
    }

    if (chain.length >= 1) {
      int i=0;

      Certificate current = chain[i];
      boolean selfSigned = isSelfSigned((X509Certificate)current);

      while (i < chain.length-1 && !selfSigned) {
        i++;
        Certificate next = chain[i];
        try {
          current.verify(next.getPublicKey());
        } catch (Exception e) {
          LogManager.log(EIDASUtil.class, e);

          return false;
        }

        current = next;
        selfSigned = isSelfSigned((X509Certificate)current);
      }

      if (selfSigned) {
        try {
          current.verify(current.getPublicKey());
        } catch (Exception e) {
          LogManager.log(EIDASUtil.class, e);

          return false;
        }
      }
    }



    return true;
  }


  public static boolean isRevoked(X509Certificate cert, Set<X509CRL> crlList) {
    for (X509CRL crl : crlList) {
      X509CRLEntry revokedCertificate = crl.getRevokedCertificate(cert.getSerialNumber());

      if(revokedCertificate !=null){
        return true;
      }
    }

    return false;
  }

  public static boolean isRevoked(X509Certificate cert, X509CRL crl) {
    X509CRLEntry revokedCertificate = crl.getRevokedCertificate(cert.getSerialNumber());

    if(revokedCertificate !=null){
      return true;
    }

    return false;
  }


  public static String toPEM(X509Certificate cert) throws Exception {
    StringWriter pem = new StringWriter();
    JcaPEMWriter pw = new JcaPEMWriter(pem);
    try {


      pw.writeObject(cert);
      pw.flush();

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (pw != null) {
        pw.close();
      }
    }


    return pem.toString();
  }

  public static String generateThumbprint(X509Certificate cert) throws NoSuchAlgorithmException, CertificateEncodingException {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    byte[] der = cert.getEncoded();
    md.update(der);
    byte[] digest = md.digest();
    String digestHex = Util.bytesToHex(digest);
    //System.out.println(DatatypeConverter.printHexBinary(digest));

    return digestHex;
  }

  public ArrayList<String> getEIDASRoles(X509Certificate cert) {
    ArrayList<String> roles = new ArrayList<>();

    // todo : implement it!


    return roles;
  }


  public static String getOrganizationID(X509Certificate cert) {
    String name = cert.getSubjectDN().getName();
    HashMap<String, String> dnMap = new HashMap<String, String>();
    String[] tokens = name.split(",");
    for (String token : tokens) {
      String[] nameVal = token.split("=");
      dnMap.put(nameVal[0].trim(), nameVal[1].trim());
    }

    String organizationNumber = dnMap.get("OID.2.5.4.97");

    return organizationNumber;
  }

  private static void toMap(HashMap<String, String> map, ASN1Primitive primitive) throws IOException {
    if (primitive instanceof ASN1Sequence) {
      ASN1Sequence sequence = (ASN1Sequence)primitive;

      ASN1Encodable[] items = sequence.toArray();

      if (items.length == 2) {
        if (items[0].toASN1Primitive() instanceof ASN1ObjectIdentifier) {
          map.put(((ASN1ObjectIdentifier) items[0].toASN1Primitive()).getId(), items[1].toASN1Primitive().toString());
          toMap(map, items[1].toASN1Primitive());

          return;
        }
      } else if (items.length == 1) {
        if (items[0].toASN1Primitive() instanceof ASN1ObjectIdentifier) {
          map.put(((ASN1ObjectIdentifier) items[0].toASN1Primitive()).getId(), items[0].toASN1Primitive().toString());

          return;
        }
      }

      for (ASN1Encodable item : items) {
        if (item instanceof ASN1Sequence) {
          toMap(map, item.toASN1Primitive());
        }
      }

    }
  }

//  public static ASN1Primitive toDERObject(byte[] data) throws IOException {
//    ByteArrayInputStream inStream = new ByteArrayInputStream(data);
//    ASN1InputStream asnInputStream = new ASN1InputStream(inStream);
//
//    return asnInputStream.readObject();
//  }



}
