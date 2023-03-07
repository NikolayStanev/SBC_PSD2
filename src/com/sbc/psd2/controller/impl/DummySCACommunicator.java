package com.sbc.psd2.controller.impl;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.controller.SCACommunicator;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.rest.PsuData;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-22
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class DummySCACommunicator implements SCACommunicator {
  public void generateOTP(ConsentOp op) throws ApplicationException {

  }

  public void generateOTP(BGNCreditTransferOp op) throws ApplicationException {

  }

  public boolean checkOTP(BGNCreditTransferOp op, String otp) throws ApplicationException {
    return true;
  }

  public boolean checkOTP(ConsentOp op, String otp) throws ApplicationException {
    return true;
  }


  private void generateOTP(String textForSigning, String description) throws ApplicationException {

  }

  private boolean checkOTP(String textForSigning, String description, String otp) throws ApplicationException {
    return true;
  }


}
