package com.sbc.common.logging.impl;


import java.util.Properties;

/**
 * User: Pavel Bonev
 * Date: 2007-2-8
 * Time: 17:48:18
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleLogger implements Logger {
  public void log(String message) {
    String logString = MessageFormatter.format(message);

    System.out.println(logString);
  }

  public void log(String message, String... args) {
    String logString = MessageFormatter.format(message, args);

    System.out.println(logString);
  }

  public void log(Properties props) {
    String logString = MessageFormatter.format(props);

    System.out.println(logString);
  }

  public void log(Throwable t) {
    String logString = MessageFormatter.format(t);

    System.out.println(logString);
  }

  public void log(byte[] message) {
    String logString = MessageFormatter.format(message);

    System.out.println(logString);
  }

  public void log(Class cl, byte[] msg) {
    String logString = MessageFormatter.format(cl, msg);

    System.out.println(logString);
  }

  public void log(Class cl, String message) {
    String logString = MessageFormatter.format(cl, message);

    System.out.println(logString);
  }

  public void log(Class cl, String message, String... args) {
    String logString = MessageFormatter.format(cl, message, args);

    System.out.println(logString);
  }

  public void log(Class cl, Properties props) {
    String logString = MessageFormatter.format(cl, props);

    System.out.println(logString);
  }

  public void log(Class cl, Throwable t) {
    String logString = MessageFormatter.format(cl, t);

    System.out.println(logString);
  }
}
