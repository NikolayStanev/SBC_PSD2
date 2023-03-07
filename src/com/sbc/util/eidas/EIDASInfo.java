package com.sbc.util.eidas;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import sun.rmi.runtime.Log;

import java.security.cert.X509Certificate;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-10
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class EIDASInfo {
  public static final String ROLE_AS = "PSP_AS";     // account servicing (Bank)
  public static final String ROLE_PI = "PSP_PI";     // payment initiation
  public static final String ROLE_AI = "PSP_AI";     // account information
  public static final String ROLE_IC = "PSP_IC";    // payment instruments (cards)

  private String tppAuthNumber;

  private HashSet<String> roles;
  private String ncaName;
  private String ncaID;

  private String thumbprint;

  private static HashSet<String> testRoles = new HashSet<>();

  static {
    testRoles.add(ROLE_PI);
    testRoles.add(ROLE_AI);
    testRoles.add(ROLE_IC);
  }

  public static EIDASInfo dummyInfo = new EIDASInfo("test", testRoles, "BNB", "BNB", "BDDC46B84F79B0F7DF4084824728460377F11170");

  public EIDASInfo(String tppAuthNumber, HashSet<String> roles, String ncaName, String ncaID, String thumbprint) {
    this.tppAuthNumber = tppAuthNumber;
    this.roles = roles;
    this.ncaName = ncaName;
    this.ncaID = ncaID;
    this.thumbprint = thumbprint;
  }

  public String getTppAuthNumber() {
    return tppAuthNumber;
  }

  public HashSet<String> getRoles() {
    return roles;
  }

  public String getNcaName() {
    return ncaName;
  }

  public String getNcaID() {
    return ncaID;
  }

  public String getThumbprint() {
    return thumbprint;
  }

  public boolean isAS() {
    return roles.contains(ROLE_AS);
  }

  public boolean isIC() {
    return roles.contains(ROLE_IC);
  }

  public boolean isPI() {
    return roles.contains(ROLE_PI);
  }

  public boolean isAI() {
    return roles.contains(ROLE_AI);
  }

  public static EIDASInfo build(X509Certificate cert) throws ApplicationException {
    String tppAuthNumber = EIDASUtil.getOrganizationID(cert);

    String thumbprint = null;

    try {
      thumbprint = EIDASUtil.generateThumbprint(cert);
    } catch (Exception e) {
      LogManager.log(EIDASInfo.class, e);
      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }

    HashSet<String> rolesSet = new HashSet<>();
    String ncaName = null;
    String ncaID = null;

    byte[] encodedExtensionValue = cert.getExtensionValue("1.3.6.1.5.5.7.1.3");

    ASN1Sequence primitive = null;
    try {
      primitive = (ASN1Sequence) JcaX509ExtensionUtils.parseExtensionValue(encodedExtensionValue);
    } catch (Exception e) {
      LogManager.log(EIDASInfo.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
    }


    ASN1Encodable[] items = primitive.toArray();
    for (ASN1Encodable item : items) {
      //System.out.println(item.getClass() + " -> " + item);
      if (item instanceof ASN1Sequence) {
        ASN1Sequence sequence = ((ASN1Sequence) item);
        ASN1Encodable[] items2 = sequence.toArray();

        if (items2[0] instanceof ASN1ObjectIdentifier) {
          ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) items2[0];

          if (oid.getId().equalsIgnoreCase("0.4.0.19495.2")) {
            ASN1Sequence data = (ASN1Sequence) items2[1];

            ASN1Encodable[] elements = data.toArray();

            ASN1Sequence rolesSeq = (ASN1Sequence) elements[0];
            ASN1Encodable[] roles = rolesSeq.toArray();

            for (ASN1Encodable role : roles) {
              ASN1Sequence rolePair = (ASN1Sequence) role;
              ASN1Encodable[] rolePairArray = rolePair.toArray();

              rolesSet.add(rolePairArray[1].toString());
            }

            ncaName = elements[1].toString();
            ncaID = elements[2].toString();


            //System.out.println(data);
          }
        }
      }
    }

    if (ncaName == null) {
      throw new ApplicationException(ApplicationException.CERTIFICATE_INVALID, "Can not find NCA Name!");
    }

    if (ncaID == null) {
      throw new ApplicationException(ApplicationException.CERTIFICATE_INVALID, "Can not find NCA ID!");
    }

    if (rolesSet.size() == 0) {
      throw new ApplicationException(ApplicationException.CERTIFICATE_INVALID, "Can not find any role!");
    }

    EIDASInfo info = new EIDASInfo(tppAuthNumber, rolesSet, ncaName, ncaID, thumbprint);


    return info;
  }

  @Override
  public String toString() {
    return "EIDASInfo{" +
            "tppAuthNumber='" + tppAuthNumber + '\'' +
            ", roles=" + roles +
            ", ncaName='" + ncaName + '\'' +
            ", ncaID='" + ncaID + '\'' +
            ", thumbprint='" + thumbprint + '\'' +
            '}';
  }
}
