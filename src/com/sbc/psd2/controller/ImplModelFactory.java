package com.sbc.psd2.controller;

import com.sbc.common.util.ScaApproach;

public interface ImplModelFactory {


    public ScaApproach getValueScaApproach();

    public CoreSystemCommunicator getCoreSystemCommunicator();

    public IdentityManagementCommunicator getIdentityManagementCommunicator();

    public SCACommunicator getScaCommunicator();

    public UserFilter getUserFilter();

}
