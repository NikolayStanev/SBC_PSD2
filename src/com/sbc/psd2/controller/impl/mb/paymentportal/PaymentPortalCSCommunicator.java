package com.sbc.psd2.controller.impl.mb.paymentportal;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.controller.impl.mb.util.MBIBCodesMapper;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.mb.MbCoreSystemAccountInfo;
import com.sbc.psd2.data.rest.*;
import oracle.jdbc.OracleTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public class PaymentPortalCSCommunicator implements CoreSystemCommunicator {
  private static final String GET_TRANSACTION_STATUS = "{ ? = call UBXPSD2GATE.GATE_TO_CORE.getTransactionStatus(?,?) }";
  private static final String GET_ACCOUNT_BALANCE = "{ ? = call UBXPSD2GATE.GATE_TO_CORE.getAccountBalances(?,?) }";
  private static final String GET_ACCOUNT_DETAILS = "{ ? = call UBXPSD2GATE.GATE_TO_CORE.getAccountDetails(?,?,?,?) }";
  private static final String MAKE_TRANSACTION = "{ ? = call UBXPSD2GATE.GATE_TO_CORE.makeTransaction(?,?,?,?,?) }";


  public String getTransactionStatus(BGNCreditTransferOp op) throws ApplicationException {
    LogManager.trace(PaymentPortalCSCommunicator.class, "getTransactionStatus()", op.getExtRefID());

    String refID = op.getExtRefID();
    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(GET_TRANSACTION_STATUS);
      ocs.registerOutParameter(1, OracleTypes.VARCHAR);
      ocs.registerOutParameter(3, OracleTypes.VARCHAR);

      ocs.setString(2, refID);
      ocs.execute();

      String status = ocs.getString(3);

      String psd2Status = PaymentPortalCodesMapper.getInstance().mapCoreSystemTransactionStatus(status);

      return psd2Status;

    } catch (SQLException e) {
      LogManager.log(PaymentPortalCSCommunicator.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");

    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }
    }
  }
//  function makeTransaction(ip_debitIBAN in varchar2, --IBAN наредител
//                           ip_creditIBAN in varchar2,  -- IBAN получател
//                           ip_amount in number,        --- сума
//                                   ip_ccy in varchar2,         --- валута / за сега само BGN /
//                                   op_fc_ref out varchar2 ) --- връща се референцията на транзацията ако е

  public String makeTransaction(BGNCreditTransferOp op) throws ApplicationException {
    LogManager.trace(PaymentPortalCSCommunicator.class, "makeTransaction()", op.toString());

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(MAKE_TRANSACTION);

      ocs.registerOutParameter(1, OracleTypes.NUMBER);
      ocs.registerOutParameter(6, OracleTypes.VARCHAR);

      ocs.setString(2, op.getDebtorAccount().getIban());
      ocs.setString(3, op.getCreditorAccount().getIban());
      ocs.setBigDecimal(4, op.getInstructedAmount().getAmount());
      ocs.setString(5, op.getInstructedAmount().getCurrency());

      ocs.execute();


      int result = ocs.getInt(1);

      LogManager.trace(PaymentPortalCSCommunicator.class, "makeTransaction() result = ", "" + result);

      if (result != 1) {
        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Can not execute transaction!");
      }

      String ref = ocs.getString(6);
      if (ref == null) {
        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Can not execute transaction!");
      }


      return ref;

    } catch (SQLException e) {
      LogManager.log(PaymentPortalCSCommunicator.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");

    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }
    }
  }

  //  function getAccountDetails (ip_iban in varchar2, --- подава се IBAN за който искаме детаили
//          op_ccy out varchar2, --- връшаме код валута на сметката ISO символен / примерно BGN /
//         op_acc_descr out varchar2, ---Връщаме име на сметката
//         op_acc_type out varchar2  ----връщаме класа на сметката
//  ) return number; --- ако всичко е Ок се връща 1 иначе -1 или -2;
  public CoreSystemAccountInfo getAccountDetails(String iban) throws ApplicationException {
    LogManager.trace(PaymentPortalCSCommunicator.class, "getAccountDetails()", iban);

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(GET_ACCOUNT_DETAILS);
      ocs.registerOutParameter(1, OracleTypes.NUMBER);
      ocs.registerOutParameter(3, OracleTypes.VARCHAR);
      ocs.registerOutParameter(4, OracleTypes.VARCHAR);
      ocs.registerOutParameter(5, OracleTypes.VARCHAR);

      ocs.setString(2, iban);
      ocs.execute();


      int result = ocs.getInt(1);

      LogManager.trace(PaymentPortalCSCommunicator.class, "getAccountDetails() result = ", "" + result);

      if (result != 1) {
        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "No data found about this IBAN!");
      }

      String currency = ocs.getString(3);
      String name = ocs.getString(4);
      String accType = ocs.getString(5);

      CoreSystemAccountInfo info = new MbCoreSystemAccountInfo(iban, currency, null, accType, name);

      return info;

    } catch (SQLException e) {
      LogManager.log(PaymentPortalCSCommunicator.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");

    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }
    }
  }


  public ArrayList<Balance> getAccountBalances(String iban) throws ApplicationException {
    LogManager.trace(PaymentPortalCSCommunicator.class, "getAccountBalances()", iban);

    ArrayList<Balance> balances = new ArrayList<>();

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(GET_ACCOUNT_BALANCE);
      ocs.registerOutParameter(1, OracleTypes.NUMBER);
      ocs.registerOutParameter(3, OracleTypes.NUMBER);

      ocs.setString(2, iban);
      ocs.execute();

      int result = ocs.getInt(1);

      LogManager.trace(PaymentPortalCSCommunicator.class, "getAccountBalances() result = ", "" + result);

      if (result != 1) {
        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "No data found about this IBAN!");
      }

      BigDecimal balance = ocs.getBigDecimal(3);
      balances.add(new Balance(BalanceTypes.AUTHORIZED, new Amount("BGN", balance)));

      return balances;

    } catch (SQLException e) {
      LogManager.log(PaymentPortalCSCommunicator.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");

    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(PaymentPortalCSCommunicator.class, ex);
        }
      }
    }
  }

  public ArrayList<CoreSystemAccountInfo> getAccounts(UserInfo userInfo) throws ApplicationException {
    LogManager.trace(PaymentPortalCSCommunicator.class, "getAccounts()", userInfo.toString());

    ArrayList<CoreSystemAccountInfo> list = new ArrayList<>();

    return list;
  }

  public boolean confirmFunds(String accIban, BigDecimal amount, String currency) throws ApplicationException {
    return true;
  }


  public Transactions readTransactionsDetails(String transactionId) throws ApplicationException {
    // todo implement it
    return null;
  }

  public ReadTransactionsListResponse readTransactionsList(String accountId, Date dateFrom, Date dateTo, String bookingStatus) throws ApplicationException {
    // todo implement it
    return null;
  }

  @Override
  public void validateIBANs(String iban, UserInfo userInfo) throws ApplicationException {

  }

  @Override
  public void validateIBANs(HashMap<String, Account> accountMap, UserInfo userInfo) throws ApplicationException {

  }

}
