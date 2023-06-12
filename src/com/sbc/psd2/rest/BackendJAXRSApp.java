package com.sbc.psd2.rest;

import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.activityLog.ActivityFilter;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.rest.resources.AccountDataResource;
import com.sbc.psd2.rest.resources.ConsentResource;
import com.sbc.psd2.rest.resources.CreditTransferResource;
import com.sbc.psd2.rest.resources.OpenResource;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.routing.Filter;

import java.util.*;


public class BackendJAXRSApp extends JaxRsApplication {

   private boolean testMode;

   private static Class<?>[] rootResources = {
            CreditTransferResource.class,
            AccountDataResource.class,
            ConsentResource.class,
            OpenResource.class

    };

    public static Class<?>[] getRoots() {
        return rootResources;
    }


    public org.restlet.Restlet createInboundRoot() {
        Restlet orig = super.createInboundRoot();

        Filter activityFilter = new ActivityFilter();
        Filter userFilter = AbstractCommunicatorFactory.getInstance().getUserFilter();

        activityFilter.setNext(userFilter);
        userFilter.setNext(orig);

        return activityFilter;
    }


    public BackendJAXRSApp(Context context) {
        super(context);

        init();

        add(new javax.ws.rs.core.Application() {
            @Override
            public Set<Class<?>> getClasses() {
                Set<Class<?>> res = new HashSet<Class<?>>(Arrays.asList(rootResources));
                return res;
            }


        });

        MyStatusService statusService = new MyStatusService();

        setStatusService(statusService);
    }


    private void init() {
      testMode = AppConfig.getInstance().isTestMode();
    }

  public boolean isTestMode() {
    return testMode;
  }
}

