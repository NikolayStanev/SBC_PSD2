package com.sbc.util.eidas;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-6
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class LRUCache {
  private Node head;
  private Node tail;
  private HashMap<String, Node> map = null;

  private static final int CAPACITY;
  private static final LRUCache instance;

  private static final long  day = 24 * 60 * 60 * 1000;

  static {
    // todo: read capacity value from some environment setting

    CAPACITY = 10;

    instance = new LRUCache();
  }



  private LRUCache() {
    this.map = new HashMap<>();
  }



  public static LRUCache getInstance() {
    return instance;
  }

  public synchronized Set<String> getKeys() {
    return map.keySet();
  }

  public X509CRL retrieve (String crlURL) throws Exception {
    Node node = map.get(crlURL);

    if (node == null || isOld(node.timeMils)) {

      CertificateFactory cf = CertificateFactory.getInstance("X509");

      URL url = new URL(crlURL);
      URLConnection connection = url.openConnection();

      X509CRL crl = null;
      try (DataInputStream inStream = new DataInputStream(connection.getInputStream())) {

        crl = (X509CRL) cf.generateCRL(inStream);

        put(crlURL,crl);
      }

      return crl;

    } else {
      return  get(crlURL);
    }
  }
  private boolean isOld(long timeMils) {
    long currentTimeMils = System.currentTimeMillis();
    long diff = currentTimeMils - timeMils;

    return diff > day;
  }

  private synchronized X509CRL get(String key) {

    //move to tail
    Node t = map.get(key);

    removeNode(t);
    offerNode(t);

    return t.value;
  }

  private synchronized void put(String key, X509CRL value) {
    if(map.containsKey(key)){
      Node t = map.get(key);
      t.value = value;

      //move to tail
      removeNode(t);
      offerNode(t);
      t.setTimeMils();
    }else{
      if(map.size() >= CAPACITY){
        //delete head
        map.remove(head.key);
        removeNode(head);
      }

      //add to tail
      Node node = new Node(key, value);
      offerNode(node);
      node.setTimeMils();
      map.put(key, node);
    }
  }

  private void removeNode(Node n){
    if(n.prev!=null){
      n.prev.next = n.next;
    }else{
      head = n.next;
    }

    if(n.next!=null){
      n.next.prev = n.prev;
    }else{
      tail = n.prev;
    }
  }

  private void offerNode(Node n){
    if(tail!=null){
      tail.next = n;
    }

    n.prev = tail;
    n.next = null;
    tail = n;

    if(head == null){
      head = tail;
    }
  }


  private static class Node {
    private String key;
    private X509CRL value;
    private long timeMils;
    Node prev;
    Node next;

    public Node(String key, X509CRL value) {
      this.key = key;
      this.value = value;
    }

    private void setTimeMils (){
      timeMils = System.currentTimeMillis();
    }

  }


}
