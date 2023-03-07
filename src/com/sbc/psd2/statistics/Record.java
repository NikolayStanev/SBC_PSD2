package com.sbc.psd2.statistics;

import java.util.Date;

/**
 * The class represent a row in PSD2_Statistics table.
 */

public class Record {

    private Date date;
    private int percentWorking;
    private int percentNotWorking;
    private int allRequests;
    private int allErrorRequests;
    private int asRequests;
    private int asErrorRequests;
    private int piRequests;
    private int piErrorRequests;
    private int icRequests;
    private int icErrorRequests;

/**
    This is the average time per request for all.
 */
    private long averageTime;
    private long averageTimeAS;
    private long averageTimePI;
    private long averageTimeCI;


    public Record() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPercentWorking() {
        return percentWorking;
    }

    public void setPercentWorking(int percentWorking) {
        this.percentWorking = percentWorking;
    }

    public int getPercentNotWorking() {
        return percentNotWorking;
    }

    public void setPercentNotWorking(int percentNotWorking) {
        this.percentNotWorking = percentNotWorking;
    }

    public int getAllRequests() {
        return allRequests;
    }

    public void setAllRequests(int allRequests) {
        this.allRequests = allRequests;
    }

    public int getAllErrorRequests() {
        return allErrorRequests;
    }

    public void setAllErrorRequests(int allErrorRequests) {
        this.allErrorRequests = allErrorRequests;
    }

    public int getAsRequests() {
        return asRequests;
    }

    public void setAsRequests(int asRequests) {
        this.asRequests = asRequests;
    }

    public int getAsErrorRequests() {
        return asErrorRequests;
    }

    public void setAsErrorRequests(int asErrorRequests) {
        this.asErrorRequests = asErrorRequests;
    }

    public int getPiRequests() {
        return piRequests;
    }

    public void setPiRequests(int piRequests) {
        this.piRequests = piRequests;
    }

    public int getPiErrorRequests() {
        return piErrorRequests;
    }

    public void setPiErrorRequests(int piErrorRequests) {
        this.piErrorRequests = piErrorRequests;
    }

    public int getIcRequests() {
        return icRequests;
    }

    public void setIcRequests(int icRequests) {
        this.icRequests = icRequests;
    }

    public int getIcErrorRequests() {
        return icErrorRequests;
    }

    public void setIcErrorRequests(int icErrorRequests) {
        this.icErrorRequests = icErrorRequests;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public long getAverageTimeAS() {
        return averageTimeAS;
    }

    public void setAverageTimeAS(long averageTimeAS) {
        this.averageTimeAS = averageTimeAS;
    }

    public long getAverageTimePI() {
        return averageTimePI;
    }

    public void setAverageTimePI(long averageTimePI) {
        this.averageTimePI = averageTimePI;
    }

    public long getAverageTimeCI() {
        return averageTimeCI;
    }

    public void setAverageTimeCI(long averageTimeCI) {
        this.averageTimeCI = averageTimeCI;
    }
}
