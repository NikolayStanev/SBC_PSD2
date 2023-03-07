package com.sbc.psd2.statistics;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.activityLog.ActivityDAO;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;

public class RecordDAO {

    private static final String SP_CREATE_RECORD = "{call ubxpsd2.psd2.createRecord(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

    public static int createRecord (Record record) {

        Connection connection = null;

        CallableStatement ocs = null;

        try {
            connection = OracleDBManager.getInstance().getConnection();

            ocs = connection.prepareCall(SP_CREATE_RECORD);
            ocs.registerOutParameter(1, OracleTypes.INTEGER);


            ocs.setDate(2, new java.sql.Date(record.getDate().getTime()));
            ocs.setInt(3, record.getPercentWorking());
            ocs.setInt(4, record.getPercentNotWorking());
            ocs.setInt(5, record.getAllRequests());
            ocs.setInt(6, record.getAllErrorRequests());
            ocs.setInt(7, record.getAsRequests());
            ocs.setInt(8, record.getAsErrorRequests());
            ocs.setInt(9, record.getPiRequests());
            ocs.setInt(10, record.getPiErrorRequests());
            ocs.setInt(11, record.getIcRequests());
            ocs.setInt(12, record.getIcErrorRequests());
            ocs.setLong(13, record.getAverageTime());
            ocs.setLong(14, record.getAverageTimeAS());
            ocs.setLong(15, record.getAverageTimePI());
            ocs.setLong(16, record.getAverageTimeCI());


            ocs.execute();

            connection.commit();

            int recordId = ocs.getInt(1);

            return recordId;

        } catch (Exception e) {
            LogManager.log(ActivityDAO.class, e);

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception ex) {
                    LogManager.log(ActivityDAO.class, ex);
                }
            }

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e, "Execution Error!");
        } finally {
            if (ocs != null) {
                try {
                    ocs.close();
                } catch (Exception ex) {
                    LogManager.log(ActivityDAO.class, ex);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    LogManager.log(ActivityDAO.class, ex);
                }
            }
        }
    }


}
