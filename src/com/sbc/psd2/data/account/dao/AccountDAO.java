package com.sbc.psd2.data.account.dao;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Util;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.creditTransfer.dao.BGNCreditTransferOpDAO;
import oracle.jdbc.internal.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

public class AccountDAO {
  private static final String SP_INSERT_ACCOUNT = "{call ubxpsd2.psd2.insertAccount(?,?,?,?,?,?,?,?,?,?)}";
  private static final String SP_GET_ACCOUNTS_PER_CONSENT = "{call ubxpsd2.psd2.getAccountsByConsentID(?,?)}";
  private static final String SP_GET_ACCOUNT = "{call ubxpsd2.psd2.getAccount(?,?,?)}";
  private static final String SP_GET_ACCOUNT_BY_IBAN = "{call ubxpsd2.psd2.getAccountByIBAN(?,?,?)}";
  private static final String SP_UPDATE_ACCOUNT = "{call ubxpsd2.psd2.updateAccount(?,?,?)}";

  public static void insertAccount(Account account, Connection connection) throws ApplicationException {
    LogManager.trace(AccountDAO.class, "insertAccount()", account.toString());

    CallableStatement ocs = null;
    try {
      ocs = connection.prepareCall(SP_INSERT_ACCOUNT);
      ocs.registerOutParameter(1, OracleTypes.INTEGER);

      ocs.setString(2, account.getIban());
      ocs.setString(3, account.getAccountId());
      ocs.setString(4, Util.boolToYN(account.getWithTransactions()));
      ocs.setString(5, Util.boolToYN(account.getWithBalances()));
      ocs.setInt(6, account.getConsentId());
      ocs.setString(7, account.getCurrency());
      ocs.setInt(8, account.getTries());
      ocs.setDate(9, new java.sql.Date(account.getCurrentDate().getTime()));
      ocs.setString(10, Util.boolToYN(account.getWithDetails()));

      ocs.execute();

    } catch (Exception e) {
      LogManager.log(AccountDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }
    }
  }


  public static void updateAccount(Account account) throws ApplicationException {
    LogManager.trace(AccountDAO.class, "updateAccount()", account.toString());

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_UPDATE_ACCOUNT);

      ocs.setInt(1, account.getId());
      ocs.setInt(2, account.getTries());
      ocs.setDate(3, new java.sql.Date(account.getCurrentDate().getTime()));

      ocs.execute();

      connection.commit();

    } catch (Exception e) {
      LogManager.log(AccountDAO.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }
    }
  }
    /*
    id,
       iban,
       account_id,
       transactions,
       balances,
       consent_id,
       currency,
       try,
       current_date,
       account_info
     */

  public static HashMap<String, Account> getAccountsByConsentID(int _consentID) throws ApplicationException {
    LogManager.trace(AccountDAO.class, "getAccountsByConsentID()", "" + _consentID);

    HashMap<String, Account> result = new HashMap<String, Account>();

    Connection connection = null;

    CallableStatement ocs = null;
    ResultSet rs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_GET_ACCOUNTS_PER_CONSENT);
      ocs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);

      ocs.setInt(1, _consentID);

      ocs.execute();

      rs = (ResultSet) ocs.getObject(2);
      while (rs.next()) {

        int dbID = rs.getInt(1);
        String iban = rs.getString(2);
        String accountID = rs.getString(3);
        String transactionsString = rs.getString(4);
        String balancesString = rs.getString(5);
        int consentID = rs.getInt(6);
        String currency = rs.getString(7);
        int tries = rs.getInt(8);
        Date currentDate = rs.getDate(9);
        String detailsString = rs.getString(10);

        boolean details = Util.ynToBool(detailsString);
        boolean transactions = Util.ynToBool(transactionsString);
        boolean balances = Util.ynToBool(balancesString);


        Account acc = new Account(dbID, iban, accountID, transactions, balances, details, consentID, currency, tries, new java.util.Date(currentDate.getTime()));
        result.put(iban, acc);
      }

      return result;
    } catch (Exception e) {
      LogManager.log(AccountDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }
    }
  }


  public static Account getAccount(String _accountID, int _consentID) throws ApplicationException {
    LogManager.trace(AccountDAO.class, "getAccount()", _accountID, "" + _consentID);

    HashMap<String, Account> result = new HashMap<String, Account>();

    Connection connection = null;

    CallableStatement ocs = null;
    ResultSet rs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_GET_ACCOUNT);
      ocs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);

      ocs.setString(1, _accountID);
      ocs.setInt(2, _consentID);

      ocs.execute();

      rs = (ResultSet) ocs.getObject(3);
      if (rs.next()) {

        int dbID = rs.getInt(1);
        String iban = rs.getString(2);
        String accountID = rs.getString(3);
        String transactionsString = rs.getString(4);
        String balancesString = rs.getString(5);
        int consentID = rs.getInt(6);
        String currency = rs.getString(7);
        int tries = rs.getInt(8);
        Date currentDate = rs.getDate(9);
        String detailsString = rs.getString(10);

        boolean details = Util.ynToBool(detailsString);
        boolean transactions = Util.ynToBool(transactionsString);
        boolean balances = Util.ynToBool(balancesString);


        Account acc = new Account(dbID, iban, accountID, transactions, balances, details, consentID, currency, tries, new java.util.Date(currentDate.getTime()));

        return acc;
      }

      return null;
    } catch (Exception e) {
      LogManager.log(AccountDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }
    }
  }

  public static Account getAccountByIBAN(String _iban, int _consentID) throws ApplicationException {
    LogManager.trace(AccountDAO.class, "getAccountByIBAN()", _iban, "" + _consentID);

    HashMap<String, Account> result = new HashMap<String, Account>();

    Connection connection = null;

    CallableStatement ocs = null;
    ResultSet rs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_GET_ACCOUNT_BY_IBAN);
      ocs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);

      ocs.setString(1, _iban);
      ocs.setInt(2, _consentID);

      ocs.execute();

      rs = (ResultSet) ocs.getObject(3);
      if (rs.next()) {

        int dbID = rs.getInt(1);
        String iban = rs.getString(2);
        String accountID = rs.getString(3);
        String transactionsString = rs.getString(4);
        String balancesString = rs.getString(5);
        int consentID = rs.getInt(6);
        String currency = rs.getString(7);
        int tries = rs.getInt(8);
        Date currentDate = rs.getDate(9);
        String detailsString = rs.getString(10);

        boolean details = Util.ynToBool(detailsString);
        boolean transactions = Util.ynToBool(transactionsString);
        boolean balances = Util.ynToBool(balancesString);


        Account acc = new Account(dbID, iban, accountID, transactions, balances, details, consentID, currency, tries, new java.util.Date(currentDate.getTime()));

        return acc;
      }

      return null;
    } catch (Exception e) {
      LogManager.log(AccountDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(AccountDAO.class, ex);
        }
      }
    }
  }
}
