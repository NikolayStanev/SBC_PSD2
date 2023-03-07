package com.sbc.psd2.controller.impl.mb.paymentportal;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.controller.AbstractCodesMapper;
import com.sbc.psd2.data.statuses.TransactionStatuses;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-4-9
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */
public class PaymentPortalCodesMapper extends AbstractCodesMapper {
  private static PaymentPortalCodesMapper instance = new PaymentPortalCodesMapper();

  private PaymentPortalCodesMapper() {
    // todo fill them all!
    addErrorMapping("errorToken", ApplicationException.TOKEN_INVALID);

    // todo fill them all!
    addTransStatusMapping("accounted", TransactionStatuses.ACCEPT_SETTLEMENT_COMPLETED);
  }


  public static PaymentPortalCodesMapper getInstance() {
    return instance;
  }
}
