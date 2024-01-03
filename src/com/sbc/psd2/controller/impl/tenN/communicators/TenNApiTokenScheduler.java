package com.sbc.psd2.controller.impl.tenN.communicators;

import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.TaskExecutor;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TenNApiTokenScheduler implements ServletContextListener {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public void contextInitialized(ServletContextEvent event) {
        LogManager.trace(getClass(), "Starting TenNApiTokenScheduler..,");

        try {
            scheduler.scheduleAtFixedRate(new ApiTokenTask(), 0, 50, TimeUnit.MINUTES);

        } catch (Exception e) {
            LogManager.log(TenNApiTokenScheduler.class, e);
        }
    }


    public void contextDestroyed(ServletContextEvent event) {
        LogManager.trace(getClass(), "contextDestroyed");

        try {
            LogManager.trace(getClass(), "Shutdown TenNApiTokenScheduler..,");
            scheduler.shutdownNow();
            LogManager.trace(getClass(), "Shutdown TenNApiTokenScheduler finished.");

            TaskExecutor.INSTANCE.stop();

        } catch (Exception e) {
            LogManager.log(getClass(), e);
        }//load data here
    }

}
