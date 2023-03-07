package com.sbc.test;



import com.sbc.common.logging.LogManager;
import com.sbc.psd2.activityLog.Activity;
import com.sbc.psd2.activityLog.ActivityDAO;
import com.sbc.psd2.rest.BackendJAXRSApp;
import com.sbc.psd2.statistics.Record;
import com.sbc.psd2.statistics.RecordDAO;
import com.sbc.psd2.statistics.StatsCollector;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sbc.psd2.statistics.StatsCollector.collectRecord;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-23
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class TestShits {
  public static void main(String args[]) throws ParseException {

    try {
      // get a calendar instance, which defaults to "now"
      Calendar calendar = Calendar.getInstance();

      // get a date to represent "today"
      Date today = calendar.getTime();

      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);

      // add one day to the date/calendar
      calendar.add(Calendar.DAY_OF_WEEK, 1);

      // now get "tomorrow"
      Date tomorrow = calendar.getTime();
      long delay = (tomorrow.getTime() - today.getTime()) / 1000 / 60;

      ScheduledExecutorService statsCollectorScheduler = Executors.newScheduledThreadPool(1);


      statsCollectorScheduler.scheduleAtFixedRate(new StatsCollector(), delay,1440, TimeUnit.MINUTES);

    }catch (Exception e) {
      LogManager.log(BackendJAXRSApp.class, e);
    }

    while (true) {
        try {
          Thread.sleep(100);
        }catch (Exception e){
          e.printStackTrace();
        }
    }

  }
}
