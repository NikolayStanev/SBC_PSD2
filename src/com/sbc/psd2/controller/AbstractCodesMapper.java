package com.sbc.psd2.controller;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.impl.mb.util.MBIBCodesMapper;
import com.sbc.psd2.data.statuses.TransactionStatuses;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-4-9
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class AbstractCodesMapper {
  private HashMap<String, Integer> errorCodesMap = new HashMap<>();
  private HashMap<String, String> statusesMap = new HashMap<>();


  protected void addErrorMapping(String code, Integer psd2Code) {
    errorCodesMap.put(code, psd2Code);
  }

  protected void addTransStatusMapping(String status, String psd2Status) {
    statusesMap.put(status, psd2Status);
  }

  public int mapErrorCode(String coreSystemError) {
    Integer code = errorCodesMap.get(coreSystemError);

    if (code == null) {
      code = ApplicationException.INTERNAL_ERROR;
    }

    return code;
  }

  public String mapCoreSystemTransactionStatus(String status) throws ApplicationException {
    String psd2Status = statusesMap.get(status);

    if (psd2Status == null) {
      LogManager.trace(MBIBCodesMapper.class, "Unknows core system transaction status: " + status);
      throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Interal Error!");
    }

    return psd2Status;
  }
}
