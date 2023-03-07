package com.sbc.common.logging.impl;

import java.io.File;
import java.io.PrintWriter;

/**
 * User: Pavel Bonev
 * Date: 2008-9-1
 * Time: 10:56:10
 * To change this template use File | Settings | File Templates.
 */
public class FileLoggerInfo {
  public int currentFileNum = 0;
  public String currentFileName = null;
  public PrintWriter writer = null;
  public File file = null;

  public String threadName = null;

  public FileLoggerInfo(String threadName) {
    this.threadName = threadName;
  }
}
