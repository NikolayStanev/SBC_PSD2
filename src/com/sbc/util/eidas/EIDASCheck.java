package com.sbc.util.eidas;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import org.bouncycastle.asn1.*;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import javax.net.ssl.KeyManagerFactory;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-1-31
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class EIDASCheck {


  public static EIDASInfo check(Certificate[] chain) throws ApplicationException {
    boolean isValid = EIDASUtil.validateSignatures(chain);
    if (!isValid) {
      if (chain == null || chain.length == 0) {
        LogManager.log(EIDASCheck.class, "Chain can not be verified! No chain!");
      } else {
        LogManager.log(EIDASCheck.class, "Chain can not be verified!" + chain);
      }

      throw new ApplicationException(401, "Certificate chain can not be verified!");
    }

    try {
      X509Certificate root = (X509Certificate)chain[chain.length-1];
      String thumbprint = EIDASUtil.generateThumbprint(root);


      String sn = root.getSubjectDN().getName();
      String thumbprint2 = EIDASUtil.getThumbprint(sn);

      if (!thumbprint.equalsIgnoreCase(thumbprint2)) {
        throw new Exception();
      }

    } catch (Exception e) {
      LogManager.log(EIDASCheck.class, e);

      throw new ApplicationException(401, "Root certificate can not be verified!");
    }


    X509Certificate eidas = (X509Certificate)chain[0];
    try {
      eidas.checkValidity();
    } catch (CertificateExpiredException e) {
      LogManager.log(EIDASCheck.class, e);
      throw new ApplicationException(401, "Client certificate is expired!");
    } catch (CertificateNotYetValidException e) {
      LogManager.log(EIDASCheck.class, e);
      throw new ApplicationException(401, "Client certificate is not yet valid!");
    }


    LRUCache cache = LRUCache.getInstance();
    for (Certificate cert : chain) {
      try {
        HashSet<String> crlURLs = EIDASUtil.getCrlDistributionPoint((X509Certificate)cert);
        for (String crlURL : crlURLs) {
          //System.out.println(crlURL);
          X509CRL crl = cache.retrieve(crlURL);
          if (crl != null) {
            if (crl.isRevoked(cert)) {
              throw new ApplicationException(ApplicationException.CERTIFICATE_REVOKED, "Cert is revoked!");
            }
          } else {
            throw new Exception("CRL Cache returned null for " + cert.toString());
          }
        }
      } catch (ApplicationException e) {
        throw e;
      } catch (Exception e) {
        LogManager.log(EIDASCheck.class, e);

        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Certificate can not be checked against CRL!");
      }
    }


      EIDASInfo info = EIDASInfo.build(eidas);


      // todo: additional checks!

      return info;
//    } catch (Exception e) {
//      LogManager.log(EIDASCheck.class, e);
//
//      throw new ApplicationException(ApplicationException.NOT_AUTHORIZED, "Can not validate eIDAS specific attributes!");
//    }
  }



}
