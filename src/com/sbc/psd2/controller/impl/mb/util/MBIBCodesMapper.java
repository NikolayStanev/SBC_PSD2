package com.sbc.psd2.controller.impl.mb.util;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.AbstractCodesMapper;
import com.sbc.psd2.data.statuses.TransactionStatuses;


import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-4-9
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public class MBIBCodesMapper extends AbstractCodesMapper {
  private static MBIBCodesMapper instance = new MBIBCodesMapper();

  private MBIBCodesMapper() {
    // todo fill them all!
    addErrorMapping("hui", ApplicationException.TOKEN_INVALID);

    // todo: refactor it
    //addErrorMapping("errStartDate", new ApplicationException(ApplicationException.FORMAT_ERROR, "Bad start date!"));

    // trans statuses from IB
//    UNSIGNED("За Подпис", "Unsigned", 0),
//    SIGNED("Подписан", "Signed", 1),
//    UPLOADED("Приет", "Uploaded", 2),
//    POSTED("Одобрен", "Posted", 3),
//    CANCELLED("Отхвърлен", "Cancelled", 4),
//    ERROR("Грешен", "Error", 5),
//    REJECTED("Неодобрен", "Rejected", 6),
//    DELETED("Изтрит", "Deleted", 7),
//    PAID("Платено", "Paid", 8),
//    PENDING("Чакащ обработка", "Pending", 9);

//    ACCEPT_CUSTOMER_PROFILE = "ACCP";
//    ACCEPT_SETTLEMENT_COMPLETED = "ACSC";
//    ACCEPT_SETTLEMENT_IN_PROCESS = "ACSP";
//    ACCEPT_TECHNICAL_VALIDATION = "ACTC";
//    ACCEPTED_WITH_CHANGE = "ACWC";
//    ACCEPTED_WITHOUT_POSTING = "ACWP";
//    RECEIVED = "RCVD";
//    PENDING = "PDNG";
//    REJECTED = "RJCT";

    addTransStatusMapping("0", TransactionStatuses.RECEIVED);
    addTransStatusMapping("1", TransactionStatuses.ACCEPT_CUSTOMER_PROFILE);
    addTransStatusMapping("2", TransactionStatuses.ACCEPT_CUSTOMER_PROFILE);
    addTransStatusMapping("3", TransactionStatuses.ACCEPT_SETTLEMENT_IN_PROCESS);
    addTransStatusMapping("4", TransactionStatuses.REJECTED);
    addTransStatusMapping("5", TransactionStatuses.REJECTED);
    addTransStatusMapping("6", TransactionStatuses.REJECTED);
    addTransStatusMapping("7", TransactionStatuses.REJECTED);
    addTransStatusMapping("8", TransactionStatuses.ACCEPT_SETTLEMENT_COMPLETED);
    addTransStatusMapping("9", TransactionStatuses.PENDING);
  }


  public static MBIBCodesMapper getInstance() {
    return instance;
  }
}
