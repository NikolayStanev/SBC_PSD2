package com.sbc.psd2.activityLog;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.data.account.dao.AccountDAO;
import com.sbc.psd2.controller.UserFilter;
import com.sbc.psd2.rest.util.Util;
import oracle.jdbc.OracleTypes;
import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.util.Series;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class ActivityDAO {

    private static final String SP_ACTIVITY_CREATE = "{call ubxpsd2.psd2.createActivity(?,?,?,?,?,?,?,?)}";
    private static final String SP_ACTIVITY_UPDATE = "{call ubxpsd2.psd2.updateActivity(?,?,?)}";
    private static final String SP_GET_ACTIVITY_BY_DATE = "{call ubxpsd2.psd2.getActivity(?,?)}";

    public static int createActivity (Request request) throws ApplicationException {

        Connection connection = null;

        CallableStatement ocs = null;

        try {
            connection = OracleDBManager.getInstance().getConnection();

            ocs = connection.prepareCall(SP_ACTIVITY_CREATE);
            ocs.registerOutParameter(1, OracleTypes.INTEGER);

            Method method = request.getMethod();
            Reference requestRef = request.getResourceRef();
            String op = method.getName() + ": " + requestRef.getPath();

            String psd2OP = Util.getPSD2Op(request);

            String ip = UserFilter.getIP(request);

            Representation requestRep = request.getEntity();

            Series headers = request.getHeaders();
            String psuId = headers.getFirstValue("psu-id", true);
            String xRequestId = headers.getFirstValue("x-request-id", true);

            ocs.setString(2, op);
            ocs.setString(3, requestRep.getText());
            ocs.setString(4, null);
            ocs.setString(5, ip);
            ocs.setString(6, psuId);
            ocs.setString(7, xRequestId);
            ocs.setString(8, psd2OP);

            ocs.execute();

            connection.commit();

            int activityId = ocs.getInt(1);

            return activityId;

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

    public static void updateActivity(int id, String responseBody, String errorFlag) throws ApplicationException {

        Connection connection = null;

        CallableStatement ocs = null;
        try {
            connection = OracleDBManager.getInstance().getConnection();

            ocs = connection.prepareCall(SP_ACTIVITY_UPDATE);

            ocs.setInt(1, id);
            ocs.setString(2, responseBody);
            ocs.setString(3, errorFlag);

            ocs.execute();

            connection.commit();

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

    public static ArrayList<Activity> getActivity (Date date) throws ApplicationException {
//        LogManager.trace(ActivityDAO.class, "getActivity()", "" + date);

        ArrayList<Activity> activities = new ArrayList<>();

        Connection connection = null;


        CallableStatement ocs = null;
        ResultSet rs = null;
        try {
            connection = OracleDBManager.getInstance().getConnection();

            ocs = connection.prepareCall(SP_GET_ACTIVITY_BY_DATE);
            ocs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);


            Timestamp timestamp = new Timestamp(date.getTime());
            ocs.setTimestamp(1,timestamp);

            ocs.execute();

            rs = (ResultSet) ocs.getObject(2);
            while (rs.next()) {

                int id = rs.getInt(1);
                String op = rs.getString(2);
                Date opTimestamp = rs.getDate(3);
                String opType = rs.getString(4);
                Date responseTime = rs.getDate(5);
                long opTimeMillis = rs.getLong(6);
                String isInternalError = rs.getString(7);

                Activity activity = new Activity(id, op, opTimestamp, opType, responseTime, opTimeMillis, isInternalError);

                activities.add(activity);
            }

            return activities;
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
