package com.sbc.psd2.activityLog;

import java.util.Date;

public class Activity {

    public static final String PSP_AI = "PSP_AI";
    public static final String PSP_CI = "PSP_CI";
    public static final String PSP_PI = "PSP_PI";

    private int id;
    private  String op;
    private Date opTimestamp;
    private String opType;
    private Date responseTime;
    private long opTimeMillis;
    private String isInternalError;

    public Activity(int id, String op, Date opTimestamp, String opType, Date responseTime, long opTimeMillis, String isInternalError) {
        this.id = id;
        this.op = op;
        this.opTimestamp = opTimestamp;
        this.opType = opType;
        this.responseTime = responseTime;
        this.opTimeMillis = opTimeMillis;
        this.isInternalError = isInternalError;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Date getOpTimestamp() {
        return opTimestamp;
    }

    public void setOpTimestamp(Date opTimestamp) {
        this.opTimestamp = opTimestamp;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public Date getResponse_time() {
        return responseTime;
    }

    public void setResponse_time(Date response_time) {
        this.responseTime = response_time;
    }

    public long getOpTimeMillis() {
        return opTimeMillis;
    }

    public void setOpTimeMillis(int opTimeMillis) {
        this.opTimeMillis = opTimeMillis;
    }

    public String getIsInternalError() {
        return isInternalError;
    }

    public void setIsInternalError(String isInternalError) {
        this.isInternalError = isInternalError;
    }
}
