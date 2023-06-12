package com.sbc.psd2.controller.impl.tenN;

import com.sbc.common.util.ScaApproach;
import com.sbc.psd2.controller.*;
import com.sbc.psd2.controller.impl.tenN.communicators.TenNCoreSystemCommunicator;
import com.sbc.psd2.controller.impl.tenN.communicators.TenNIdentityManagementCommunicator;
import com.sbc.psd2.controller.impl.tenN.communicators.TenNSCACommunicator;

public class TenNModelFactory extends AbstractCommunicatorFactory {


    public TenNModelFactory() {

        scaApproach = ScaApproach.DECOUPLED;
        coreSystemCommunicator = new TenNCoreSystemCommunicator();
        identityManagementCommunicator = new TenNIdentityManagementCommunicator();
        scaCommunicator = new TenNSCACommunicator();
        userFilter = new TenNUserFilter();

    }

}
