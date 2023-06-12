package com.sbc.psd2.controller.impl;

import com.sbc.common.util.ScaApproach;
import com.sbc.psd2.controller.*;

public class DummyModelFactory extends AbstractCommunicatorFactory {

    public DummyModelFactory() {

        scaApproach = ScaApproach.DECOUPLED;
        coreSystemCommunicator = new DummyCoreSystemCommunicator();
        identityManagementCommunicator = new DummyIdentityManagementCommunicator();
        scaCommunicator = new DummySCACommunicator();
        userFilter = new DummyUserFilter();

    }
}
