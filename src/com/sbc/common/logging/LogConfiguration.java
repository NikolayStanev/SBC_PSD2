package com.sbc.common.logging;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 18-6-1
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class LogConfiguration {
  public static final String MODE_TRACE = "TRACE";
  public static final String MODE_DEBUG = "DEBUG";
  public static final String MODE_PRODUCTION = "PRODUCTION";

  public static final String DEST_CONSOLE = "CONSOLE";
  public static final String DEST_FILE = "FILE";
  public static final String DEST_NONE = "NONE";

  public static final int DEFAULT_MAX_SIZE = 10;
  public static final int DEFAULT_MAX_NUM = 20;
  public static final String DEFAULT_LOG_DIR = "./logs";
  public static final String DEFAULT_MODE = MODE_DEBUG;
  public static final String DEFAULT_DEST = DEST_CONSOLE;


  private boolean logFilePerThread = false;
  private boolean logAppendToLast = false;
  private String logDir = DEFAULT_LOG_DIR;
  private String logMode = DEFAULT_MODE;
  private String logDest = DEFAULT_DEST;
  private long maxFileLength = 10;
  private int maxFileNum = 10;

  public LogConfiguration() {
  }

  public LogConfiguration(boolean logFilePerThread, boolean logAppendToLast, String logDir, String logMode, String logDest, long maxFileLength, int maxFileNum) {
    this.logFilePerThread = logFilePerThread;
    this.logAppendToLast = logAppendToLast;
    this.logDir = logDir;
    this.logMode = logMode;
    this.logDest = logDest;
    this.maxFileLength = maxFileLength;
    this.maxFileNum = maxFileNum;
  }

  public synchronized static LogConfiguration buildInstance() {
    LogConfiguration instance = null;

    try {
      InitialContext initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      instance = (LogConfiguration) envCtx.lookup("conf/logConfig");
    } catch (Exception e) {
      e.printStackTrace();
      instance = new LogConfiguration();
    }

    return instance;
  }

  public boolean isLogFilePerThread() {
    return logFilePerThread;
  }

  public boolean isLogAppendToLast() {
    return logAppendToLast;
  }

  public String getLogDir() {
    return logDir;
  }

  public String getLogMode() {
    return logMode;
  }

  public String getLogDest() {
    return logDest;
  }

  public long getMaxFileLength() {
    return maxFileLength;
  }

  public int getMaxFileNum() {
    return maxFileNum;
  }

  public void setLogFilePerThread(boolean logFilePerThread) {
    this.logFilePerThread = logFilePerThread;
  }

  public void setLogAppendToLast(boolean logAppendToLast) {
    this.logAppendToLast = logAppendToLast;
  }

  public void setLogDir(String logDir) {
    this.logDir = logDir;
  }

  public void setLogMode(String logMode) {
    this.logMode = logMode;
  }

  public void setLogDest(String logDest) {
    this.logDest = logDest;
  }

  public void setMaxFileLength(long maxFileLength) {
    this.maxFileLength = maxFileLength;
  }

  public void setMaxFileNum(int maxFileNum) {
    this.maxFileNum = maxFileNum;
  }


}
