package com.sbc.test;

import com.sbc.util.eidas.LRUCache;

import java.security.cert.X509CRL;

public class TestLRUCache {


    public static void main (String[] args) {

        try {
            System.out.println(testRetrieve().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static X509CRL testRetrieve () throws Exception {

        LRUCache cache = LRUCache.getInstance();
        cache.retrieve("http://crl.b-trust.org/repository/B-TrustRootACA.crl");
        return cache.retrieve("http://crl.b-trust.org/repository/B-TrustRootACA.crl");
    }

}
