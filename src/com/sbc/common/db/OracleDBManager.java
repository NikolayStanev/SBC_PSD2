package com.sbc.common.db;

import com.sbc.common.logging.LogManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-1-10
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
public class OracleDBManager {
  /*
  Allianz test db - jdbc:oracle:thin:@10.56.113.7:1521:flexlive
  Municipality Bank DEV Oracle server - jdbc:oracle:thin:@172.16.52.41:flexlive
   */
  private static String urlDb = "jdbc:oracle:thin:@192.168.110.189:1521:flexlive";
  private static String username = "UBXPSD2";
  private static String password = "UBXPSD2";

  private DataSource ds = null;

  private static final OracleDBManager instance = new OracleDBManager();

  private OracleDBManager() {
    LogManager.trace(getClass(), "OracleDBManager()");

    try {
      InitialContext initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      ds = (DataSource) envCtx.lookup("jdbc/psd2");
    } catch (Exception e) {
      LogManager.log(getClass(), e);

      try {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
      } catch (Exception ex) {
        LogManager.log(getClass(), ex);
      }
    }
  }

  public synchronized static OracleDBManager getInstance() {
    return instance;
  }

  public Connection getConnection() throws SQLException {
    LogManager.trace(getClass(), "getConnection()");

//    Exception e = new Exception();
//    LogManager.trace(getClass(), e);

    Connection con = null;

    if (ds != null) {

      con = ds.getConnection();
    } else {
      con = DriverManager.getConnection(urlDb, username, password);
    }


    if (!con.isClosed() && con.isValid(1)) {
      con.setAutoCommit(false);

      return con;
    } else {
      throw new SQLException("Connection is not valid!");
    }
  }
}
