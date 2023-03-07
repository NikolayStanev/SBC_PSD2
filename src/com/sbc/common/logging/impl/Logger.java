package com.sbc.common.logging.impl;

import java.util.Properties;

/**
 * User: Pavel Bonev
 * Date: 2007-2-8
 * Time: 17:46:06
 * To change this template use File | Settings | File Templates.
 */
public interface Logger {
   public void log(Class cl, byte[] msg);
   public void log(Class cl, String message);
   public void log(Class cl, String message, String... args);
   public void log(Class cl, Properties props);
   public void log(Class cl, Throwable t);

   public void log(byte[] msg);
   public void log(String message);
   public void log(String message, String... args);
   public void log(Properties p);
   public void log(Throwable t);
}
