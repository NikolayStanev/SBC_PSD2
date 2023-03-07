package com.sbc.psd2.statistics;

import com.sbc.common.logging.LogManager;
import com.sbc.psd2.activityLog.Activity;
import com.sbc.psd2.activityLog.ActivityDAO;
import com.sbc.psd2.rest.BackendJAXRSApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StatsCollector implements Runnable {

    private static final long NO_RESPONSE_TIMER = 30*1000;
    private static final long DAY = 24 * 60 * 60 * 1000;


    /**
     * Collects data from the activity table for the given date.
     * @param date should be in (DD-MM-YYYY)
     * @return a record of the activities for the given date.
     */
    public static Record collectRecord (Date date) {


        ArrayList<Activity> activities = ActivityDAO.getActivity(date);

        long noAnswerCounter = 0;
        boolean isTimerStarted = false;
        boolean isNotWorking = false;
        long start = 0;

        long timeNotWorking = 0;

        int counterAllRequests = 0;
        int counterAllErrorRequests = 0;
        int counterASRequests = 0;
        int counterASErrorRequests = 0;
        int counterPIRequests = 0;
        int counterPIErrorRequests = 0;
        int counterICRequests = 0;
        int counterICErrorRequests = 0;

        long averageTime = 0;
        long averageTimeAs = 0;
        long averageTimePI = 0;
        long averageTimeIC = 0;


        for (Activity activity : activities) {
            if (activity.getIsInternalError().equals("N")) {

                if(isNotWorking) {

                    isNotWorking = false;
                    noAnswerCounter = 0;

                    timeNotWorking = timeNotWorking + (activity.getOpTimestamp().getTime() - start);

                    start = 0;
                }

                isTimerStarted = false;

                counterAllRequests++;
                averageTime += activity.getOpTimeMillis();

                switch (activity.getOpType()) {
                    case Activity.PSP_AI:
                        counterASRequests++;
                        averageTimeAs += activity.getOpTimeMillis();

                        break;

                    case Activity.PSP_PI:
                        counterPIRequests++;
                        averageTimePI += activity.getOpTimeMillis();

                        break;

                    case Activity.PSP_CI:
                        counterICRequests++;
                        averageTimePI += activity.getOpTimeMillis();

                        break;
                }
            } else {
                counterAllErrorRequests++;

                if (isTimerStarted && activity.getResponse_time() == null) {
                    if (isInRange(start, activity.getOpTimestamp().getTime()) && noAnswerCounter < 5) {
                        noAnswerCounter++;
                        if (noAnswerCounter == 5) {
                            isNotWorking = true;
                        }
                    }else {
                        isNotWorking = true;
                    }

                } else if (activity.getResponse_time() == null) {

                    start = activity.getOpTimestamp().getTime();
                    isTimerStarted = true;
                    noAnswerCounter++;

                }else if (isNotWorking && activity.getResponse_time() != null){
                    isNotWorking = false;
                    noAnswerCounter = 0;
                    isTimerStarted = false;

                } else {
                    isTimerStarted = false;
                    noAnswerCounter = 0;
                }

                switch (activity.getOpType()) {
                    case Activity.PSP_AI:
                        counterASErrorRequests++;

                        break;
                    case Activity.PSP_PI:
                        counterPIErrorRequests++;

                        break;
                    case Activity.PSP_CI:
                        counterICErrorRequests++;

                        break;
                }
            }

        }
        if (counterAllRequests != 0) {
            averageTime = averageTime /(long) counterAllRequests;
        }
        if (counterASRequests != 0) {
            averageTimeAs = averageTimeAs /(long) counterASRequests;
        }
        if (counterPIRequests != 0) {
            averageTimePI = averageTimePI /(long) counterPIRequests;
        }
        if (counterICRequests != 0) {
            averageTimeIC = averageTimeIC /(long) counterICRequests;
        }

        int percentNotWorking = (int)((timeNotWorking * 100)/ DAY );
        int percentWorking = 100 - percentNotWorking;


        Record record = new Record();
        record.setDate(date);
        record.setPercentWorking(percentWorking);
        record.setPercentNotWorking(percentNotWorking);
        record.setAllRequests(counterAllRequests);
        record.setAllErrorRequests(counterAllErrorRequests);
        record.setAsRequests(counterASRequests);
        record.setAsErrorRequests(counterASErrorRequests);
        record.setPiRequests(counterPIRequests);
        record.setPiErrorRequests(counterPIErrorRequests);
        record.setIcRequests(counterICRequests);
        record.setIcErrorRequests(counterICErrorRequests);
        record.setAverageTime(averageTime);
        record.setAverageTimeAS(averageTimeAs);
        record.setAverageTimePI(averageTimePI);
        record.setAverageTimeCI(averageTimeIC);

        return record;

    }

    private static boolean isInRange (long start, long current) {

        return start - current <= NO_RESPONSE_TIMER;

    }


    @Override
    public void run() {
        Date date = null;

        try {
            SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");

            date = sf.parse(sf.format(new Date()));
//            date = sf.parse("17-02-2020");
            Record record = collectRecord(date);

            RecordDAO.createRecord(record);

        } catch (Exception e) {
            LogManager.log(StatsCollector.class, e);
        }
    }
}
