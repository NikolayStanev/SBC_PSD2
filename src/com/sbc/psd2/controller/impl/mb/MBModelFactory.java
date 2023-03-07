package com.sbc.psd2.controller.impl.mb;

import com.sbc.common.util.ScaApproach;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;

public class MBModelFactory extends AbstractCommunicatorFactory {

    public MBModelFactory() {

        scaApproach = ScaApproach.DECOUPLED;
        coreSystemCommunicator = new MBIBCommunicator();
        identityManagementCommunicator = new MBIdentityManagementCommunicator();
        scaCommunicator = new OTPSSCommunicator();
        userFilter = new MBUserFilter();

        coreSystemCommunicatorEndPoint = "";
        identityManagementCommunicatorEndPoint = "";
        scaCommunicatorEndPoint = "";

    }

}
