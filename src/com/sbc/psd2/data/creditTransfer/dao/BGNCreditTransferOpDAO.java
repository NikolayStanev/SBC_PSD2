package com.sbc.psd2.data.creditTransfer.dao;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.data.*;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.rest.AccountDetails;
import com.sbc.psd2.data.rest.Amount;
import com.sbc.psd2.data.rest.IBAN;
import com.sbc.psd2.data.rest.PaymentType;
import oracle.jdbc.internal.OracleTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-23
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
public class BGNCreditTransferOpDAO {
  private static final String SP_INSERT_BGN_TRANSFER_OP = "{call ubxpsd2.psd2.insertBGNTrans(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
  private static final String SP_UPDATE_BGN_TRANSFER_OP = "{call ubxpsd2.psd2.updateBGNTrans(?,?,?,?,?,?)}";
  private static final String SP_GET_BGN_TRANSFER_OP_BY_PMNT_ID = "{call ubxpsd2.psd2.getBGNTransByPmntID(?,?,?)}";

  public static BGNCreditTransferOp createTransferOp(BGNCreditTransferOp op) throws ApplicationException {
    LogManager.trace(BGNCreditTransferOpDAO.class, "createTransferOp()", op.toString() );

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_INSERT_BGN_TRANSFER_OP);
      ocs.registerOutParameter(1, OracleTypes.INTEGER);

      ocs.setString(2, op.getCommonData().getxRequestID());
      ocs.setString(3, op.getCommonData().getIp());
      ocs.setString(4, op.getCommonData().getPsuIPAddress());
      ocs.setString(5, op.getDebtorAccount().getIban().getIban());
      ocs.setString(6, op.getInstructedAmount().getCurrency());
      ocs.setBigDecimal(7, op.getInstructedAmount().getAmount());
      ocs.setString(8, op.getCreditorAccount().getIban().getIban());
      ocs.setString(9, op.getCreditorName());
      ocs.setString(10, op.getTransactionStatus());
      ocs.setString(11, op.getPaymentId());
      ocs.setString(12, op.getCommonData().getPsuID());
      if (op.getPaymentType() != null) {
        ocs.setString(13, op.getPaymentType().getServiceLevel());
      } else {
        ocs.setString(13, null);
      }
      ocs.setString(14, op.getRemittanceInformationUnstructured());
      ocs.setString(15, op.getCommonData().getConsentID());
      ocs.setString(16, op.getCommonData().getTppID());

      ocs.execute();

      connection.commit();

      int id = ocs.getInt(1);

      op.setDbID(id);

      return op;
    } catch (SQLException e) {
      LogManager.log(BGNCreditTransferOpDAO.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }

      if (e.getErrorCode() == 20260) {
        throw new ApplicationException(ApplicationException.PAYMENT_FAILED, "Payment request with this paymentID is already created!");
      } else if (e.getErrorCode() == 20261) {
        throw new ApplicationException(ApplicationException.PAYMENT_FAILED, "Payment with this X_REQUEST_ID is already created!");
      } else {
        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");
      }
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }
    }
  }


  public static void update(BGNCreditTransferOp op) throws ApplicationException {
    LogManager.trace(BGNCreditTransferOpDAO.class, "updateStatus()", op.toString() );

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_UPDATE_BGN_TRANSFER_OP);

      ocs.setInt(1, op.getDbID());
      ocs.setString(2, op.getTransactionStatus());
      ocs.setString(3, op.getExtRefID());
      ocs.setString(4,op.getCustomerNumber());
      ocs.setString(5, op.getTransactionFee());
      ocs.setString(6, op.getTransactionFeeCurrency());
      ocs.execute();

      connection.commit();
    } catch (SQLException e) {
      LogManager.log(BGNCreditTransferOpDAO.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }
    }
  }


  public static BGNCreditTransferOp getOpByPaymentID(String _paymentID, String tppID) throws ApplicationException {
    LogManager.trace(BGNCreditTransferOpDAO.class, "getOpByPaymentID()", _paymentID);

    Connection connection = null;

    CallableStatement ocs = null;
    ResultSet rs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs =  connection.prepareCall(SP_GET_BGN_TRANSFER_OP_BY_PMNT_ID);
      ocs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);

      ocs.setString(1, _paymentID);
      ocs.setString(2, tppID);

      ocs.execute();

      rs = (ResultSet) ocs.getObject(3);
      if (rs.next()) {

        int dbID = rs.getInt(1);
        String xRequestID = rs.getString(2);
        String ip = rs.getString(3);
        String psuIPAddress = rs.getString(4);
        String debitIBANString = rs.getString(5);
        String currency = rs.getString(6);
        BigDecimal amount = rs.getBigDecimal(7);
        String creditIBANString = rs.getString(8);
        String creditorName = rs.getString(9);
        String transactionStatus = rs.getString(10);
        String paymentID = rs.getString(11);
        String extRefID = rs.getString(12);
        java.sql.Date logDate = rs.getDate(13);
        String psuID = rs.getString(14);
        String serviceLevel = rs.getString(15);
        String remittanceInformationUnstructured = rs.getString(16);
        String consentID = rs.getString(17);
//      tppID = rs.getString(18);
        String customerNumber = rs.getString(18);
        String transactionFee = rs.getString(19);
        String transactionFeeCurrency = rs.getString(20);

        IBAN debitIBAN = new IBAN(debitIBANString);
        IBAN creditIBAN = new IBAN(creditIBANString);

        Amount instructedAmount = new Amount(currency, amount);
        AccountDetails debtorAccountDetails = new AccountDetails(debitIBAN);
        AccountDetails creditorAccountDetails = new AccountDetails(creditIBAN);
        PaymentType paymentType = new PaymentType(serviceLevel);

        PSD2RequestCommonData commonData = new PSD2RequestCommonData(xRequestID, psuID, psuIPAddress, ip, consentID, tppID);

        return new BGNCreditTransferOp(dbID, extRefID, logDate, instructedAmount,
                debtorAccountDetails, creditorName,
                creditorAccountDetails, transactionStatus,
                paymentID, paymentType,
                remittanceInformationUnstructured,
                customerNumber,
                transactionFee,
                transactionFeeCurrency,
                commonData);
      }

      return null;
    } catch (Exception e) {
      LogManager.log(BGNCreditTransferOpDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution problem!");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }

      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(BGNCreditTransferOpDAO.class, ex);
        }
      }
    }
  }


  public static void main(String[] args) {
    try {
      String pmntID = "3e9d417c-0cc4-428f-b55c-b5085fac298b";
      String tppID = "test";
      BGNCreditTransferOp op = BGNCreditTransferOpDAO.getOpByPaymentID(pmntID, tppID);

      op.setCreditorName("Киро Щангата");
      op.setPaymentId("dksjfdkjfdklsjflas");
      op.getCommonData().setxRequestID("xreq123");
      BGNCreditTransferOpDAO.createTransferOp(op);

      System.out.println(op);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
