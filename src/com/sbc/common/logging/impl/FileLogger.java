package com.sbc.common.logging.impl;



import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Properties;

/**
 * User: Pavel Bonev
 * Date: 2007-2-8
 * Time: 17:50:18
 * To change this template use File | Settings | File Templates.
 */
public class FileLogger implements Logger {
  private static String ALL_THREADS = "all";

  private String dir = null;
  private long maxSize = 0;
  private int maxNum = 0;

  private static String pathSep = System.getProperty("file.separator");
  private static String pref = "log_";
  private static String suff = ".txt";

  private boolean console = false;
  private boolean filePerThread = false;
  private boolean appendToLast = false;

  private HashMap<String, FileLoggerInfo> threadFiles = new HashMap<String, FileLoggerInfo>();

  public FileLogger(String dir, long maxSizeInMB, int maxNum, boolean filePerThread, boolean appendToLast) {
    this.dir = dir;
    this.maxSize = maxSizeInMB*1024*1024;
    this.maxNum = maxNum;

    this.filePerThread = filePerThread;
    this.appendToLast = appendToLast;

    adjustDir();
    //changeWriter();
  }

  public void log(String message) {
    String logString = MessageFormatter.format(message);
    _log(logString);
  }

  public void log(String message, String... args) {
    String logString = MessageFormatter.format(message, args);
    _log(logString);
  }

  public void log(Properties props) {
    String logString = MessageFormatter.format(props);
    _log(logString);
  }

  public void log(byte[] message) {
    String logString = MessageFormatter.format(message);
    _log(logString);
  }

  public void log(Throwable t) {
    String logString = MessageFormatter.format(t);
    _log(logString);
  }

  public void log(Class claz, String message) {
    String logString = MessageFormatter.format(claz, message);
    _log(logString);
  }

  public void log(Class claz, String message, String... args) {
    String logString = MessageFormatter.format(claz, message, args);
    _log(logString);
  }

  public void log(Class claz, Properties props) {
    String logString = MessageFormatter.format(claz, props);
    _log(logString);
  }

  public void log(Class claz, byte[] message) {
    String logString = MessageFormatter.format(claz, message);
    _log(logString);
  }

  public void log(Class claz, Throwable t) {
    String logString = MessageFormatter.format(claz, t);
    _log(logString);
  }


  private void _log(String message) {
    FileLoggerInfo info = getPropperInfo();
    info.writer.println(message);
    info.writer.flush();
    if (!console) {
      changeWriter(info);
    }
  }


  private synchronized FileLoggerInfo getPropperInfo() {
    String threadName = ALL_THREADS;
    if (filePerThread) {
      threadName = Thread.currentThread().getName();
    }

    FileLoggerInfo fileInfo = threadFiles.get(threadName);
    if (fileInfo == null) {
      fileInfo = new FileLoggerInfo(threadName);

      if (appendToLast) {
        setLastWriter(fileInfo);
      } else {
        setWriter(fileInfo);
      }

      threadFiles.put(threadName, fileInfo);
    }

    return fileInfo;
  }

  private synchronized void changeWriter(FileLoggerInfo info) {
    if (info.file != null && info.file.length() < maxSize) {
      return;
    }

    if (info.writer != null) {
      info.writer.close();
    }

    info.currentFileNum++;
    if (maxNum <= 0 || info.currentFileNum >= maxNum) {
      info.currentFileNum = 0;
    }

    setWriter(info);
  }

  private void setWriter(FileLoggerInfo info) {
    try {
      info.currentFileName = dir + pathSep + pref + info.threadName + "_" + info.currentFileNum + suff;
      info.file = new File(info.currentFileName);
      info.writer = new PrintWriter(new FileOutputStream(info.file));
    } catch (Exception e) {
      info.writer = new PrintWriter(System.out);
      console = true;
      log(getClass(), e);
    }
  }

  private void setLastWriter(FileLoggerInfo info) {
    try {
      info.currentFileName = dir + pathSep + pref + info.threadName + "_" + info.currentFileNum + suff;
      info.file = new File(info.currentFileName);
      while (info.file.length() >= maxSize && info.currentFileNum < (maxNum - 1)) {
        info.currentFileNum++;
        info.currentFileName = dir + pathSep + pref + info.threadName + "_" + info.currentFileNum + suff;
        info.file = new File(info.currentFileName);
      }

      info.writer = new PrintWriter(new FileOutputStream(info.file, true));
    } catch (Exception e) {
      info.writer = new PrintWriter(System.out);
      console = true;
      log(e);
    }
  }

  private void adjustDir() {
    File file = new File(dir);
    if (!file.exists()) {
      file.mkdirs();
    }
  }



}
