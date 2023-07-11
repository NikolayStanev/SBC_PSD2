package com.sbc.psd2.controller.impl;

import com.sbc.common.db.OracleDBManager;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.mb.MbCoreSystemAccountInfo;
import com.sbc.psd2.data.rest.*;
import oracle.jdbc.OracleTypes;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-22
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */

// class for communication with FlexCube
public class DummyCoreSystemCommunicator implements CoreSystemCommunicator {
  public String getTransactionStatus(BGNCreditTransferOp op) throws ApplicationException {
    return "ACCT";
  }

  public String makeTransaction(BGNCreditTransferOp op) throws ApplicationException {
    String ref = "abc1234567890bca";

    return ref;
  }

  public CoreSystemAccountInfo getAccountDetails(String iban) throws ApplicationException {

    CoreSystemAccountInfo info = new MbCoreSystemAccountInfo(iban, "BGN", "PROD1", "ACC_GENERAL", "Ivan Petrov");

    return info;
  }

  public ArrayList<Balance> getAccountBalances(String iban) throws ApplicationException {
    ArrayList<Balance> balances = new ArrayList<>();


    BigDecimal balance = new BigDecimal("123.45");
    balances.add(new Balance(BalanceTypes.AUTHORIZED, new Amount("BGN", balance)));

    return balances;
  }

  public ArrayList<CoreSystemAccountInfo> getAccounts(UserInfo userInfo) throws ApplicationException {
    LogManager.trace(getClass(), "getAccounts()", userInfo.toString());

    ArrayList<CoreSystemAccountInfo> list = new ArrayList<>();

    CoreSystemAccountInfo info1 = new MbCoreSystemAccountInfo("BG11123456789100", "BGN", "PROD1", "TYPE_A", "Ivan Petrov");
    CoreSystemAccountInfo info2 = new MbCoreSystemAccountInfo("BG11123456789200", "USD", "PROD1", "TYPE_B", "Ivan Petrov");
    CoreSystemAccountInfo info3 = new MbCoreSystemAccountInfo("BG11123456789300", "EUR", "PROD1", "TYPE_C", "Ivan Petrov");

    list.add(info1);
    list.add(info2);
    list.add(info3);

    return list;
  }

  public boolean confirmFunds(String accIban, BigDecimal amount, String currency) throws ApplicationException {
    return true;
  }

  public Transactions readTransactionsDetails(String transactionId) throws ApplicationException {
    // todo implement it
    return null;
  }

  public ReadTransactionsListResponse readTransactionsList(String accountId, Date dateFrom, Date dateTo, String bookingStatus) throws ApplicationException {
    // todo implement it
    return null;
  }

  @Override
  public void validateIBANs(String iban, UserInfo userInfo) throws ApplicationException {

  }

  @Override
  public void validateIBANs(HashMap<String, Account> accountMap, UserInfo userInfo) throws ApplicationException {

  }

}
