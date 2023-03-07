package com.sbc.psd2.rest.resources;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.account.AccountLinks;
import com.sbc.psd2.data.account.dao.AccountDAO;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.consent.dao.ConsentOpDAO;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.rest.*;
import com.sbc.psd2.data.rest.Transactions;

import com.sbc.psd2.data.rest.links.TransactionLinks;
import com.sbc.psd2.rest.Headers;
import com.sbc.psd2.controller.UserFilter;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Path("/accounts")
public class AccountDataResource {

  @GET
  @Path("/")
  @Produces("application/json")
  public Accounts getAccounts(@QueryParam("withBalance") Boolean withBalance) throws ApplicationException {
    LogManager.trace(getClass(), "getAccounts");

    // result is hold here!
    Accounts accounts = new Accounts();

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    String consentID = headers.getRequestHeaderValue(Headers.HEADER_CONSENT_ID);

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    ConsentOp consent = ConsentOpDAO.getConsentByConsentID(consentID, tppID);

    ConsentOp.checkConsentValid(consent);

    HashMap<String, Account> consentAccounts = consent.getAccountMap();

    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();

    boolean depletedFlag = false;
    for (Account account : consentAccounts.values()) {
      synchronized (account.getAccountId().intern()) {
        if (account.getWithDetails() && account.updateTriesVsConsent(consent)) {
          CoreSystemAccountInfo csInfo = csCommunicator.getAccountDetails(account.getIban());
          AccountDAO.updateAccount(account);

          AccountDetails info = new AccountDetails();

          info.setResourceId(account.getAccountId());
          info.setIban(new IBAN(csInfo.getIban()));
          info.setCurrency(csInfo.getCurrency());
          info.setProduct(csInfo.getProduct());
          info.setCashAccountType(csInfo.getCashAccountType());
          info.setName(csInfo.getName());

          AccountLinks links = AccountLinks.buildLinks(account);
          info.set_links(links);

          accounts.addAccountDetails(info);
        } else {
          depletedFlag = true;
        }
      }
    }

    if (depletedFlag && accounts.getAccounts().size() == 0) {
      throw new ApplicationException(ApplicationException.ACCESS_EXCEEDED, "Maximum access quota is reached or has no rights to get details!");
    }

    return accounts;
  }

  @GET
  @Path("/{account-id}")
  @Produces("application/json")
  public AccountDetails getAccountDetails(@PathParam("account-id") String accountID, @QueryParam("withBalance") Boolean withBalance) throws ApplicationException {
    LogManager.trace(getClass(), "getAccountDetails");

    Headers headers = UserFilter.getHeaders();

    String consentID = headers.getRequestHeaderValue(Headers.HEADER_CONSENT_ID);

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    ConsentOp consent = ConsentOpDAO.getConsentByConsentID(consentID, tppID);

    ConsentOp.checkConsentValid(consent);

    Account account = AccountDAO.getAccount(accountID, consent.getDbId());

    if (account == null) {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "No account for that consent!");
    }

    if (!account.getWithDetails()) {
      throw new ApplicationException(ApplicationException.SERVICE_INVALID, "Has no rights to get details of this account!");
    }

    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
    synchronized (account.getAccountId().intern()) {
      if (account.updateTriesVsConsent(consent)) {
        CoreSystemAccountInfo csInfo = csCommunicator.getAccountDetails(account.getIban());

        AccountDAO.updateAccount(account);

        AccountDetails info = new AccountDetails();

        info.setResourceId(account.getAccountId());
        info.setIban(new IBAN(csInfo.getIban()));
        info.setCurrency(csInfo.getCurrency());
        info.setProduct(csInfo.getProduct());
        info.setCashAccountType(csInfo.getCashAccountType());
        info.setName(csInfo.getName());

        AccountLinks links = AccountLinks.buildLinks(account);
        info.set_links(links);

        return info;
      } else {
        throw new ApplicationException(ApplicationException.ACCESS_EXCEEDED, "Maximum access quota is reached!");
      }
    }
  }

  @GET
  @Path("/{account-id}/balances")
  @Produces("application/json")
  public AccountBalanceResponse getAccountBalance(@PathParam("account-id") String accountID) throws ApplicationException {
    LogManager.trace(getClass(), "getBalance");

    Headers headers = UserFilter.getHeaders();

    String consentID = headers.getRequestHeaderValue(Headers.HEADER_CONSENT_ID);

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    ConsentOp consent = ConsentOpDAO.getConsentByConsentID(consentID, tppID);

    ConsentOp.checkConsentValid(consent);

    Account account = AccountDAO.getAccount(accountID, consent.getDbId());

    if (account == null) {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "No account for that consent!");
    }

    if (!account.getWithBalances()) {
      throw new ApplicationException(ApplicationException.SERVICE_INVALID, "Has no rights to get balance of this account!");
    }

    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
    synchronized (account.getAccountId().intern()) {
      if (account.updateTriesVsConsent(consent)) {
        ArrayList<Balance> balances = csCommunicator.getAccountBalances(account.getIban());

        String iban = account.getIban();
        AccountBalanceResponse accountBalanceResponse = new AccountBalanceResponse(new AccountReference(iban), balances);

        AccountDAO.updateAccount(account);

        return accountBalanceResponse;
      } else {
        throw new ApplicationException(ApplicationException.ACCESS_EXCEEDED, "Maximum access quota is reached!");
      }
    }
  }

  @GET
  @Path("/{account-id}/transactions")
  @Produces("application/json")
  public ReadTransactionsListResponse readTransactionList (@PathParam("account-id") String accountId, @QueryParam("dateFrom") Date dateFrom, @QueryParam("dateTo") Date dateTo, @QueryParam("bookingStatus") String bookingStatus){

    LogManager.trace(getClass(), "readTransactionDetails");

    Headers headers = UserFilter.getHeaders();

    String consentID = headers.getRequestHeaderValue(Headers.HEADER_CONSENT_ID);

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    ConsentOp consent = ConsentOpDAO.getConsentByConsentID(consentID, tppID);

    ConsentOp.checkConsentValid(consent);

    Account account = AccountDAO.getAccount(accountId, consent.getDbId());

    if (account == null) {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "No account for that consent!");
    }

    if (!account.getWithTransactions()) {
      throw new ApplicationException(ApplicationException.SERVICE_INVALID, "Has no rights to get transactions of this account!");
    }

    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
    ReadTransactionsListResponse transactionsList = csCommunicator.readTransactionsList(accountId, dateFrom, dateTo, bookingStatus);

    transactionsList.setAccount(new AccountReference(account.getIban()));
    transactionsList.set_links(TransactionLinks.buildLinks(accountId));

    return transactionsList;

  }

  @GET
  @Path("/{account-id}/transactions/{resourceId}")
  @Produces("application/json")
  public Transactions readTransactionDetails (@PathParam("account-id") String accountId, @PathParam("resourceId") String resourceId){

    LogManager.trace(getClass(), "readTransactionDetails");

    Headers headers = UserFilter.getHeaders();

    String consentID = headers.getRequestHeaderValue(Headers.HEADER_CONSENT_ID);

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    ConsentOp consent = ConsentOpDAO.getConsentByConsentID(consentID, tppID);

    ConsentOp.checkConsentValid(consent);

    Account account = AccountDAO.getAccount(accountId, consent.getDbId());

    if (account == null) {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "No account for that consent!");
    }

    if (!account.getWithTransactions()) {
      throw new ApplicationException(ApplicationException.SERVICE_INVALID, "Has no rights to get transactions of this account!");
    }

    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
    Transactions transactionsDetails = csCommunicator.readTransactionsDetails(resourceId);

    return transactionsDetails;

  }
}
