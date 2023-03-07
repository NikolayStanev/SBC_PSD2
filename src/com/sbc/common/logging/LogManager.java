package com.sbc.common.logging;



import com.sbc.common.logging.impl.ConsoleLogger;
import com.sbc.common.logging.impl.FileLogger;
import com.sbc.common.logging.impl.Logger;
import com.sbc.common.logging.impl.NullLogger;

import java.util.Properties;

/**
 * User: Pavel Bonev
 * Date: 2007-1-18
 * Time: 10:37:31
 * To change this template use File | Settings | File Templates.
 */
public class LogManager {
  private static boolean logFilePerThread = false;
  private static boolean logAppendToLast = false;
  private static String logDir = LogConfiguration.DEFAULT_LOG_DIR;
  private static String logMode = LogConfiguration.DEFAULT_MODE;
  private static long maxSize = 10;
  private static int maxNum = 10;
  private static String logDest = null;

  private static final Logger NULL_LOGGER = new NullLogger();
  private static final Logger CONSOLE_LOGGER = new ConsoleLogger();

  private static Logger logger = CONSOLE_LOGGER;
  private static Logger tracer = CONSOLE_LOGGER;
  private static Logger printer = CONSOLE_LOGGER;

  static {
    LogConfiguration logConfig = LogConfiguration.buildInstance();

    logDir = logConfig.getLogDir();
    logMode = logConfig.getLogMode();
    logDest = logConfig.getLogDest();
    maxSize = logConfig.getMaxFileLength();
    maxNum = logConfig.getMaxFileNum();

    logFilePerThread = logConfig.isLogFilePerThread();
    logAppendToLast = logConfig.isLogAppendToLast();

    setLogDir(logDir);
    setLogDest(logDest);
    setLogMode(logMode);
  }

  public static void setLogDir(String logDir) {
    if (logDir == null) {
      logDir = LogConfiguration.DEFAULT_LOG_DIR;
    }

    LogManager.logDir = logDir;
  }

  public static void setLogMode(String _logMode) {
    if (_logMode == null) {
      _logMode = LogConfiguration.DEFAULT_MODE;
    }

    if (_logMode.equals(LogConfiguration.MODE_TRACE)) {
      tracer = printer;
      logger = printer;
    } else if (_logMode.equals(LogConfiguration.MODE_DEBUG)) {
      tracer = NULL_LOGGER;
      logger = printer;
    } else {
      tracer = NULL_LOGGER;
      logger = NULL_LOGGER;
    }

    LogManager.logMode = _logMode;
  }

  public static void setMaxSize(long maxSize) {
    LogManager.maxSize = maxSize;
  }

  public static void setMaxNum(int maxNum) {
    LogManager.maxNum = maxNum;
  }

  public static void setLogDest(String _logDest) {
    if (_logDest == null) {
      _logDest = LogConfiguration.DEFAULT_DEST;
    }

    if (_logDest.equals(LogConfiguration.DEST_CONSOLE)) {
      printer = CONSOLE_LOGGER;
    } else if (_logDest.equals(LogConfiguration.DEST_FILE)) {
      printer = new FileLogger(logDir, maxSize, maxNum, logFilePerThread, logAppendToLast);
    } else if (_logDest.equals(LogConfiguration.DEST_NONE)) {
      printer = NULL_LOGGER;
    }

    LogManager.logDest = _logDest;

    setLogMode(logMode);
  }

  public static void log(Class claz, String message) {
    logger.log(claz, message);
  }

  public static void log(Class claz, String message, String... args) {
    logger.log(claz, message, args);
  }

  public static void log(Class claz, Properties props) {
    logger.log(claz, props);
  }

  public static void log(Class claz, Throwable t) {
    logger.log(claz, t);
  }

  public static void log(Class claz, byte[] msg) {
    logger.log(claz, msg);
  }

  public static void trace(Class claz, byte[] msg) {
    tracer.log(claz, msg);
  }

  public static void trace(Class claz, String msg) {
    tracer.log(claz, msg);
  }

  public static void trace(Class claz, String message, String... args) {
    printer.log(claz, message, args);
  }

  public static void trace(Class claz, Properties p) {
    tracer.log(claz, p);
  }

  public static void trace(Class claz, Throwable t) {
    tracer.log(claz, t);
  }

  public static void print(byte[] msg) {
    printer.log(msg);
  }

  public static void print(String msg) {
    printer.log(msg);
  }

  public static void print(String message, String... args) {
    printer.log(message, args);
  }

  public static void print(Properties p) {
    printer.log(p);
  }

  public static void print(Throwable t) {
    printer.log(t);
  }


  public static boolean isTraceMode() {
    return (logMode.equals(LogConfiguration.MODE_TRACE));
  }

  private static Class whichClassCalledMe() {
    Throwable t = new Throwable();
    StackTraceElement[] trace = t.getStackTrace();
    String className = trace[2].getClassName();
    Class whoCalledMe = null;
    try {
      whoCalledMe = Class.forName(className);
    } catch (Exception e) {
    }

    return whoCalledMe;
  }

}
