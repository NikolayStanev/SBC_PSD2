package com.sbc.psd2.controller.impl.tenN.communicators;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.controller.SCACommunicator;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;

public class TenNSCACommunicator implements SCACommunicator {
    @Override
    public void generateOTP(ConsentOp op) throws ApplicationException {

    }

    @Override
    public void generateOTP(BGNCreditTransferOp op) throws ApplicationException {

    }

    @Override
    public boolean checkOTP(BGNCreditTransferOp op, String otp) throws ApplicationException {
        return false;
    }

    @Override
    public boolean checkOTP(ConsentOp op, String otp) throws ApplicationException {
        return false;
    }
}
