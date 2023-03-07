package com.sbc.psd2.controller;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public interface SCACommunicator {
  public void generateOTP(ConsentOp op) throws ApplicationException;

  public void generateOTP(BGNCreditTransferOp op) throws ApplicationException;

  public boolean checkOTP(BGNCreditTransferOp op, String otp) throws ApplicationException;

  public boolean checkOTP(ConsentOp op, String otp) throws ApplicationException;

}
