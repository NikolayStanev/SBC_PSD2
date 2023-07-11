package com.sbc.psd2.controller;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.rest.Amount;
import com.sbc.psd2.data.rest.Balance;
import com.sbc.psd2.data.rest.ReadTransactionsListResponse;
import com.sbc.psd2.data.rest.Transactions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-13
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
public interface CoreSystemCommunicator {
  public String getTransactionStatus(BGNCreditTransferOp op) throws ApplicationException;
  public String makeTransaction(BGNCreditTransferOp op) throws ApplicationException;
  public ArrayList<CoreSystemAccountInfo> getAccounts(UserInfo userInfo) throws ApplicationException;
  public CoreSystemAccountInfo getAccountDetails(String iban) throws ApplicationException;
  public ArrayList<Balance> getAccountBalances(String iban) throws ApplicationException;
  public Transactions readTransactionsDetails(String transactionId) throws ApplicationException;
  public ReadTransactionsListResponse readTransactionsList(String accountId, Date dateFrom, Date dateTo, String bookingStatus) throws ApplicationException;

  public void validateIBANs(String iban, UserInfo userInfo) throws ApplicationException;

  public void validateIBANs(HashMap<String, Account> accountMap, UserInfo userInfo) throws ApplicationException;

  public boolean confirmFunds(String accIban, BigDecimal amount, String currency) throws ApplicationException;
//
//  public Amount getTaxes(BGNCreditTransferOp op) throws ApplicationException;

}