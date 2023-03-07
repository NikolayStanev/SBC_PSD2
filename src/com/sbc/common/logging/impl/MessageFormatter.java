package com.sbc.common.logging.impl;

import com.sbc.common.util.Util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * User: Pavel Bonev
 * Date: 2007-2-8
 * Time: 18:03:14
 * To change this template use File | Settings | File Templates.
 */
public class MessageFormatter {

  public static String NL = System.getProperty("line.separator");
  private static char SPACE = ' ';
  private static char TAB = '\t';
  //private static char LA = '{';
  //private static char RA = '}';
  private static char EQ = '=';
  private static char SHARP = '#';
  private static char SEMICOLON = ';';



  public static String format(Class cl, String message, String args[]) {
    StringBuilder sb = new StringBuilder();
    sb.append(message);
    sb.append(" -> ");
    for (int i = 0; i < args.length - 1; i++) {
      sb.append(args[i]);
      sb.append(SEMICOLON);
    }
    sb.append(args[args.length - 1]);

    String timeString = Util.now();
    String classString = cl.getName();
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + classString +
            SHARP + threadName + SHARP + SPACE + sb.toString();

    return log;
  }


  public static String format(Class cl, byte[] message) {
    String timeString = Util.now();
    String classString = cl.getName();
    String messageString = dumpBytes(message);
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + classString +
            SHARP + threadName + SHARP + SPACE + messageString;
    return log;
  }

  public static String format(Class cl, Throwable t) {
    String timeString = Util.now();
    String classString = cl.getName();
    String messageString = t.getMessage()+'\n'+dump(t);
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + classString +
            SHARP + threadName + SHARP + SPACE + messageString;
    return log;
  }

  public static String format(Class cl, String msg) {
    String timeString = Util.now();
    String classString = cl.getName();
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + classString +
            SHARP + threadName + SHARP + SPACE + msg;
    return log;
  }

  public static String format(Class cl, Properties p) {
    String timeString = Util.now();
    String classString = cl.getName();
    String messageString = dump(p);
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + classString +
            SHARP + threadName + SHARP + SPACE + messageString;
    return log;
  }


  public static String format(byte[] message) {
    String timeString = Util.now();
    String messageString = dumpBytes(message);
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + threadName + SHARP + SPACE + messageString;
    return log;
  }


  public static String format(Throwable t) {
    String timeString = Util.now();
    String messageString = dump(t);
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + threadName + SHARP + SPACE + messageString;
    return log;
  }

  public static String format(String msg) {
    String timeString = Util.now();
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + threadName + SHARP + SPACE + msg;
    return log;
  }

  public static String format(String msg, String args[]) {
    StringBuilder sb = new StringBuilder();
    sb.append(msg);
    sb.append(" -> ");
    for (int i = 0; i < args.length - 1; i++) {
      sb.append(args[0]);
      sb.append(',');
    }
    sb.append(args[args.length - 1]);

    String timeString = Util.now();
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + threadName + SHARP + SPACE + sb.toString();
    return log;
  }

  public static String format(Properties p) {
    String timeString = Util.now();
    String messageString = dump(p);
    String threadName = Thread.currentThread().getName();

    String log = timeString + SHARP + threadName + SHARP + SPACE + messageString;
    return log;
  }

  public static String dump(Properties p) {
    StringBuffer buf = new StringBuffer();

    Iterator iterat = p.entrySet().iterator();
    while (iterat.hasNext()) {
      Map.Entry entry = (Map.Entry) iterat.next();
      buf.append(SPACE);
      buf.append(entry.getKey());
      buf.append(EQ);
      buf.append(entry.getValue());
    }

    return buf.toString();
  }

  public static String dump(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);

    sw.flush();
    String str = sw.toString();
    try {
      sw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return str;
  }


  public static String dumpBytes(byte[] bytes) {
    StringBuffer ascii = new StringBuffer();
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < bytes.length; i++) {
      if (i % 8 == 0) {
        buf.append(TAB);
        buf.append(ascii.toString());
        buf.append(NL);
        ascii = new StringBuffer();
      }

      buf.append(SPACE);
      buf.append(Util.byteToHex(bytes[i]));

      ascii.append((char) bytes[i]);
    }
    buf.append(TAB);
    buf.append(ascii.toString());
    buf.append(NL);

    return buf.toString();
  }


}
