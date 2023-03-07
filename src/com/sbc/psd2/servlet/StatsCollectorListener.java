package com.sbc.psd2.servlet;

import com.sbc.common.logging.LogManager;
import com.sbc.psd2.statistics.StatsCollector;


import javax.servlet.ServletContextEvent;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-4-15
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public class StatsCollectorListener implements javax.servlet.ServletContextListener {
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public void contextInitialized(ServletContextEvent event) {
    LogManager.trace(getClass(), "contextInitialized");

    try {
      long now = System.currentTimeMillis();
      Date date = new Date(now - TimeZone.getDefault().getRawOffset() - now % (24 * 60 * 60 * 1000));
      Date tomorrowMid = new Date(date.getTime() + 24 * 60 * 60 * 1000);

      long diff = (tomorrowMid.getTime() - now) /1000 / 60;

      scheduler.scheduleAtFixedRate(new StatsCollector(), diff, 1440, TimeUnit.MINUTES);

    } catch (Exception e) {
      LogManager.log(StatsCollectorListener.class, e);
    }
  }


  public void contextDestroyed(ServletContextEvent event) {
    LogManager.trace(getClass(), "contextDestroyed");

    try {
      LogManager.trace(getClass(), "Shutdown stats collecting..,");
      scheduler.shutdownNow();
      LogManager.trace(getClass(), "Shutdown stats collecting finished.");
    } catch (Exception e) {
      LogManager.log(getClass(), e);
    }//load data here
  }
}