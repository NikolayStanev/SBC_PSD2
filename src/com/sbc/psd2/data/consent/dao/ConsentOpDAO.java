package com.sbc.psd2.data.consent.dao;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Util;
import com.sbc.psd2.data.PSD2RequestCommonData;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.account.dao.AccountDAO;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.creditTransfer.dao.BGNCreditTransferOpDAO;
import oracle.jdbc.internal.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConsentOpDAO {

  private static final String SP_INSERT_CONSENT = "{call ubxpsd2.psd2.insertConsent(?,?,?,?,?,?,?,?,?,?)}";
  private static final String SP_GET_CONSENT_BY_ID = "{call ubxpsd2.psd2.getConsentByID(?,?)}";
  private static final String SP_GET_CONSENT_BY_CONSENTID = "{call ubxpsd2.psd2.getConsentByConsentID(?,?,?)}";
  private static final String SP_UPDATE_CONSENT_STATUS = "{call ubxpsd2.psd2.updateConsentStatus(?,?)}";
  private static final String SP_DELETE_CONSENT = "{call ubxpsd2.psd2.deleteConsent(?,?)}";

  public static ConsentOp createConsent(ConsentOp op) throws ApplicationException {
    LogManager.trace(ConsentOpDAO.class, "createConsent()", op.toString());

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      java.sql.Date dd = new java.sql.Date(op.getValidUntil().getTime());

      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_INSERT_CONSENT);
      ocs.registerOutParameter(1, OracleTypes.INTEGER);

      ocs.setString(2, op.getCommonData().getxRequestID());
      ocs.setString(3, op.getCommonData().getPsuID());
      ocs.setString(4, Util.boolToYN(op.getRecurringIndicator()));
      ocs.setDate(5, dd);
      ocs.setInt(6, op.getFrequencyPerDay());
      ocs.setString(7, Util.boolToYN(op.getCombinedServiceIndicator()));
      ocs.setString(8, op.getConsentId());
      ocs.setString(9, op.getConsentStatus());
      ocs.setString(10, op.getCommonData().getTppID());

      ocs.execute();

      int id = ocs.getInt(1);

      op.setDbId(id);


      HashMap<String, Account> accounts = op.getAccountMap();

      for (Map.Entry<String, Account> entry : accounts.entrySet()) {
        Account account = entry.getValue();
        account.setConsentId(id);
        AccountDAO.insertAccount(account, connection);
      }

      connection.commit();


      return op;
    } catch (SQLException e) {
      LogManager.log(ConsentOpDAO.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (e.getErrorCode() == 20270) {
        throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Consent with this consentID is already created!");
      } else if (e.getErrorCode() == 20271) {
        throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Consent with this X_REQUEST_ID is already created!");
      } else {
        throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");
      }
    } catch (Exception e) {
      LogManager.log(ConsentOpDAO.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }
    }
  }

  public static ConsentOp getConsentByConsentID(String consentID, String tppID) throws ApplicationException {
    LogManager.trace(ConsentOpDAO.class, "getConsentByConsentID()", consentID);

    Connection connection = null;

    CallableStatement ocs = null;
    ResultSet rs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_GET_CONSENT_BY_CONSENTID);
      ocs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);

      ocs.setString(1, consentID);
      ocs.setString(2, tppID);

      ocs.execute();

      rs = (ResultSet) ocs.getObject(3);
      if (rs.next()) {

//                SELECT id,
//        x_request_id,
//        psu_id,
//        recurring_indicator,
//        valid_until,
//        frequency_per_day,
//        combined_service_indicator,
//        consent_id,
//        consent_status

        int dbID = rs.getInt(1);
        String xRequestID = rs.getString(2);
        String psuId = rs.getString(3);
        Boolean recurringIndicator = Util.ynToBool(rs.getString(4));
        Date validUntil = rs.getDate(5);
        int frequencyPerDay = rs.getInt(6);
        Boolean combinedServiceIndicator = Util.ynToBool(rs.getString(7));
        String consentId = rs.getString(8);
        String consentStatus = rs.getString(9);
        Date creationDate = rs.getDate(10);
        tppID = rs.getString(11);

        PSD2RequestCommonData commonData = new PSD2RequestCommonData(xRequestID, psuId, null, null, consentId, tppID);

        HashMap<String, Account> accountMap = AccountDAO.getAccountsByConsentID(dbID);

        return new ConsentOp(dbID, accountMap, recurringIndicator, validUntil,
                frequencyPerDay, consentStatus, consentId, commonData, combinedServiceIndicator, creationDate);
      }

      return null;
    } catch (Exception e) {
      LogManager.log(ConsentOpDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }
    }
  }

  public static ConsentOp getConsentByID(int _id) throws ApplicationException {
    LogManager.trace(ConsentOpDAO.class, "getConsentByID()", "" + _id);

    Connection connection = null;

    CallableStatement ocs = null;
    ResultSet rs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_GET_CONSENT_BY_ID);
      ocs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);

      ocs.setInt(1, _id);

      ocs.execute();

      rs = (ResultSet) ocs.getObject(2);
      if (rs.next()) {

        int dbID = rs.getInt(1);
        String xRequestID = rs.getString(2);
        String psuId = rs.getString(3);
        Boolean recurringIndicator = Util.ynToBool(rs.getString(4));
        Date validUntil = rs.getDate(5);
        int frequencyPerDay = rs.getInt(6);
        Boolean combinedServiceIndicator = Util.ynToBool(rs.getString(7));
        String consentId = rs.getString(8);
        String consentStatus = rs.getString(9);
        Date creationDate = rs.getDate(10);
        String tppID = rs.getString(11);

        PSD2RequestCommonData commonData = new PSD2RequestCommonData(xRequestID, psuId, null, null, consentId, tppID);

        HashMap<String, Account> accountMap = AccountDAO.getAccountsByConsentID(dbID);

        return new ConsentOp(dbID, accountMap, recurringIndicator, validUntil,
                frequencyPerDay, consentStatus, consentId, commonData, combinedServiceIndicator, creationDate);
      }

      return null;
    } catch (Exception e) {
      LogManager.log(ConsentOpDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }
    }
  }

  public static void updateConsentStatus(int dbId, String consentStatus) throws ApplicationException {
    LogManager.trace(ConsentOpDAO.class, "updateConsentStatus()", consentStatus);

    Connection connection = null;

    CallableStatement ocs = null;
    ResultSet rs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_UPDATE_CONSENT_STATUS);

      ocs.setInt(1, dbId);
      ocs.setString(2, consentStatus);

      ocs.execute();

      connection.commit();

    } catch (Exception e) {
      LogManager.log(ConsentOpDAO.class, e);

      if (connection != null) {
        try {
          connection.rollback();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }
    }
  }
  //TODO: test it
  public static void deleteConsent(String consentID, String tppID) throws ApplicationException {
    LogManager.trace(ConsentOpDAO.class, "deleteConsent()", consentID);

    Connection connection = null;

    CallableStatement ocs = null;
    try {
      connection = OracleDBManager.getInstance().getConnection();

      ocs = connection.prepareCall(SP_DELETE_CONSENT);

      ocs.setString(1, consentID);
      ocs.setString(2, tppID);

      ocs.execute();

    } catch (Exception e) {
      LogManager.log(ConsentOpDAO.class, e);

      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
    } finally {
      if (ocs != null) {
        try {
          ocs.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          LogManager.log(ConsentOpDAO.class, ex);
        }
      }
    }
  }
}
