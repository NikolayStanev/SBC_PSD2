package com.sbc.psd2.controller;

import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Implementation;
import com.sbc.common.util.ScaApproach;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.controller.impl.DummyModelFactory;
import com.sbc.psd2.controller.impl.mb.MBModelFactory;
import com.sbc.psd2.controller.impl.tenN.TenNModelFactory;

public abstract class AbstractCommunicatorFactory implements ImplModelFactory{

    protected ScaApproach scaApproach;
    protected CoreSystemCommunicator coreSystemCommunicator;
    protected IdentityManagementCommunicator identityManagementCommunicator;
    protected SCACommunicator scaCommunicator;
    protected UserFilter userFilter;

    protected String coreSystemCommunicatorEndPoint;
    protected String identityManagementCommunicatorEndPoint ;
    protected String scaCommunicatorEndPoint;


    private static AbstractCommunicatorFactory instance = null;

    public static AbstractCommunicatorFactory getInstance() {

        if (instance == null) {
           instance = buildInstance();

            LogManager.trace(instance.getClass(), "ImplModelFactory was build: " +  instance.getClass());
        }

        return instance;

    }
    private static AbstractCommunicatorFactory buildInstance () {
        LogManager.trace(AbstractCommunicatorFactory.class,"Building ImplModelFactory...");

        AppConfig appConfig = AppConfig.buildInstance();
        Implementation implementation = appConfig.getImplementation();

        switch (implementation) {

            case MB:
                return new MBModelFactory();

            case TenN:
               return new TenNModelFactory();

            case Dummy:
                return new DummyModelFactory();

        }

        return new DummyModelFactory();
    }

    @Override
    public ScaApproach getValueScaApproach() {
        return scaApproach;
    }

    @Override
    public CoreSystemCommunicator getCoreSystemCommunicator() {
        return coreSystemCommunicator;
    }

    @Override
    public IdentityManagementCommunicator getIdentityManagementCommunicator() {
        return identityManagementCommunicator;
    }

    @Override
    public SCACommunicator getScaCommunicator() {
        return scaCommunicator;
    }

    @Override
    public UserFilter getUserFilter() {
        return userFilter;
    }

    @Override
    public String getCoreSystemCommunicatorEndPoint() {
        return coreSystemCommunicatorEndPoint;
    }

    @Override
    public String getIdentityManagementCommunicatorEndPoint() {
        return identityManagementCommunicatorEndPoint;
    }

    @Override
    public String getScaCommunicatorEndPoint() {
        return scaCommunicatorEndPoint;
    }
}
