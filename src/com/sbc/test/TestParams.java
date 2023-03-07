package com.sbc.test;

import com.sbc.common.db.OracleDBManager;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-3-4
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */
public class TestParams {
  public static void checkFile() throws Exception{

  }


  public static void main(String[] args) {


    try {
      File f = new File("D:\\");

      System.out.println("Before calling method: " + f.getAbsolutePath());

      //checkFile(f);

      System.out.println("After calling method: " + f.getAbsolutePath());

    } catch (Exception e) {
      e.printStackTrace();

    }
  }
}
