package com.sbc.psd2.data.consent;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.common.util.Util;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.statuses.ConsentStatuses;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.PSD2RequestCommonData;
import com.sbc.psd2.data.consent.dao.ConsentOpDAO;
import com.sbc.psd2.data.rest.AccountAccess;
import com.sbc.psd2.data.rest.AccountReference;
import com.sbc.psd2.controller.UserFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ConsentOp {
  // 20 minutes to authorize a consent
  private static final long AUTHORIZATION_TIMEOUT_IN_MILLIS = 20 * 60 * 1000;

  private int dbId = -1;

  //  Mandatory
  private Boolean recurringIndicator;
  private Date validUntil;
  private int frequencyPerDay;

  private String consentStatus;
  private String consentId;

  private PSD2RequestCommonData commonData;

  private HashMap<String, Account> accountMap = new HashMap<>();

  // Optional
  private Boolean combinedServiceIndicator = false;

  private Date creationDate;




  public ConsentOp(int dbId, HashMap<String, Account> accountMap, Boolean recurringIndicator, Date validUntil,
                   int frequencyPerDay, String consentStatus, String consentId, PSD2RequestCommonData commonData,
                   Boolean combinedServiceIndicator, Date creationDate) {
    this.dbId = dbId;
    this.recurringIndicator = recurringIndicator;
    this.validUntil = validUntil;
    this.frequencyPerDay = frequencyPerDay;
    this.consentStatus = consentStatus;
    this.consentId = consentId;
    this.commonData = commonData;
    this.combinedServiceIndicator = combinedServiceIndicator;
    this.accountMap = accountMap;
    this.creationDate = creationDate;
  }

  public static ConsentOp buildConsentOp(ConsentRequest request) {
    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();

    PSD2RequestCommonData commonInfo = new PSD2RequestCommonData(request.getxRequestID(), request.getPsuID(),
            request.getPsuIPAddress(), request.getIp(), request.getConsentID(), tppID);

    String consentId = UUID.randomUUID().toString();
    HashMap<String, Account> accountsMap = prepareAccountsForDao(request.getAccess());

    boolean recurringIndicator = true;

    if (request.getRecurringIndicator() != null && !request.getRecurringIndicator()) {
      recurringIndicator = false;
    }

    boolean combinedServiceIndicator = true;

    if (request.getCombinedServiceIndicator() != null && !request.getCombinedServiceIndicator()) {
      combinedServiceIndicator = false;
    }

    ConsentOp op = new ConsentOp(-1, accountsMap, recurringIndicator, request.getValidUntil(),
            request.getFrequencyPerDay(), ConsentStatuses.RECEIVED, consentId,
            commonInfo, combinedServiceIndicator, new Date());


    op.setAccountMap(accountsMap);


    return op;
  }

//  public void validateIBANs(UserInfo userInfo) throws ApplicationException {
//    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
//
//    ArrayList<CoreSystemAccountInfo> list = csCommunicator.getAccounts(userInfo);
//
//    for (String iban : accountMap.keySet()) {
//      boolean found = false;
//      for (CoreSystemAccountInfo info : list) {
//        if (info.getIban().equalsIgnoreCase(iban)) {
//          //fill in if there is branch in the coreSystem acc info.
////          if (info.getBranch() != null) {
////
////            Account account = accountMap.get(iban);
////            account.setBranch(info.getBranch());
////
////            accountMap.replace(iban,account);
////          }
//
//          found = true;
//          break;
//
//        }
//      }
//
//      if (!found) {
//        throw new ApplicationException(ApplicationException.CONSENT_INVALID, "IBANs are not owned by the same user!");
//      }
//    }
//
//  }

  private static HashMap<String, Account> prepareAccountsForDao(AccountAccess access) {

    HashMap<String, Account> accountsMap = new HashMap<>();

    if (access.getBalances() != null) {
      for (AccountReference accountReference : access.getBalances()) {
        String iban = accountReference.getIban();

        Account account = accountsMap.get(iban);

        if (account == null) {
          String accountId = UUID.randomUUID().toString();

          account = new Account(-1, iban, accountId, false, true,
                  false, -1, accountReference.getCurrency(), 0, new Date());
          accountsMap.put(iban, account);
        }

        account.setWithBalances(true);

        // if it is opened for balances info it is opened for detailed info regarding spec
        account.setWithDetails(true);
      }
    }

    if (access.getTransactions() != null) {
      for (AccountReference accountReference : access.getTransactions()) {
        String iban = accountReference.getIban();

        Account account = accountsMap.get(iban);

        if (account == null) {
          String accountId = UUID.randomUUID().toString();

          account = new Account(-1, iban, accountId, true, false,
                  false, -1, accountReference.getCurrency(), 0, new Date());
          accountsMap.put(iban, account);
        }

        account.setWithTransactions(true);

        // if it is opened for balances info it is opened for detailed info regarding spec
        account.setWithDetails(true);
      }
    }

    if (access.getAccounts() != null) {
      for (AccountReference accountReference : access.getAccounts()) {
        String iban = accountReference.getIban();

        Account account = accountsMap.get(iban);

        if (account == null) {
          String accountId = UUID.randomUUID().toString();

          account = new Account(-1, iban, accountId, false, false,
                  true, -1, accountReference.getCurrency(), 0, new Date());
          accountsMap.put(iban, account);
        }

        account.setWithDetails(true);
      }
    }

    return accountsMap;

  }


  public int getDbId() {
    return dbId;
  }

  public void setDbId(int dbId) {
    this.dbId = dbId;
  }

  public Boolean getRecurringIndicator() {
    return recurringIndicator;
  }

  public void setRecurringIndicator(Boolean recurringIndicator) {
    this.recurringIndicator = recurringIndicator;
  }

  public Date getValidUntil() {
    return validUntil;
  }

  public void setValidUntil(Date validUntil) {
    this.validUntil = validUntil;
  }

  public int getFrequencyPerDay() {
    return frequencyPerDay;
  }

  public void setFrequencyPerDay(int frequencyPerDay) {
    this.frequencyPerDay = frequencyPerDay;
  }

  public String getConsentStatus() {
    return consentStatus;
  }

  public void setConsentStatus(String consentStatus) {
    this.consentStatus = consentStatus;
  }

  public String getConsentId() {
    return consentId;
  }

  public void setConsentId(String consentId) {
    this.consentId = consentId;
  }

  public PSD2RequestCommonData getCommonData() {
    return commonData;
  }

  public void setCommonData(PSD2RequestCommonData commonData) {
    this.commonData = commonData;
  }

  public Boolean getCombinedServiceIndicator() {
    return combinedServiceIndicator;
  }


  public void setCombinedServiceIndicator(Boolean combinedServiceIndicator) {
    this.combinedServiceIndicator = combinedServiceIndicator;
  }

  public HashMap<String, Account> getAccountMap() {
    return accountMap;
  }

  public void setAccountMap(HashMap<String, Account> accountMap) {
    this.accountMap = accountMap;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public void checkOp() throws ApplicationException {
    // check if expired
    if (!isValidUntil()) {
      if (getConsentStatus().equalsIgnoreCase(ConsentStatuses.RECEIVED) || getConsentStatus().equalsIgnoreCase(ConsentStatuses.VALID)) {
        setConsentStatus(ConsentStatuses.EXPIRED);

        ConsentOpDAO.updateConsentStatus(getDbId(), getConsentStatus());
      }

      LogManager.trace(getClass(), "checkOp", "Expired by given validity period " + consentId );

      throw new ApplicationException(ApplicationException.CONSENT_EXPIRED, "Expired resource by validity period!");
    }

    // check if authorized in a time frame
    long diff = System.currentTimeMillis() - creationDate.getTime();
    if (getConsentStatus().equalsIgnoreCase(ConsentStatuses.RECEIVED) && diff > AUTHORIZATION_TIMEOUT_IN_MILLIS) {
      LogManager.trace(getClass(), "checkOp", "Expired by time: " + consentId );

      setConsentStatus(ConsentStatuses.REJECTED);

      ConsentOpDAO.updateConsentStatus(getDbId(), getConsentStatus());

      throw new ApplicationException(ApplicationException.CONSENT_EXPIRED, "Expired resource! Time for authorization is passed!");
    }

    if (getConsentStatus().equalsIgnoreCase(ConsentStatuses.EXPIRED)) {
      LogManager.trace(getClass(), "checkOp", "Expired: " + consentId );

      throw new ApplicationException(ApplicationException.CONSENT_EXPIRED, "Expired resource!");
    }

    if (getConsentStatus().equalsIgnoreCase(ConsentStatuses.REJECTED)) {
      LogManager.trace(getClass(), "checkOp", "Rejected: " + consentId );

      throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Rejected resource!");
    }

    if (getConsentStatus().equalsIgnoreCase(ConsentStatuses.REVOKED_BY_PSU)) {
      LogManager.trace(getClass(), "checkOp", "Revoked by PSU: " + consentId );

      throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Revoked by PSU!");
    }

    if (getConsentStatus().equalsIgnoreCase(ConsentStatuses.TERMINATED_BY_TPP)) {
      LogManager.trace(getClass(), "checkOp", "Terminated by TPP: " + consentId );

      throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Terminated by TPP!");
    }
  }

  public static void checkConsentValid(ConsentOp consent) throws ApplicationException {
    if (consent == null) {
      LogManager.trace(ConsentOp.class, "checkConsentValid", "Not found!");

      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "No consent found!");
    }

    if (consent.getCommonData().getTppID() == null || !consent.getCommonData().getTppID().equals(UserFilter.getEIDASInfo().getTppAuthNumber())) {
      LogManager.trace(ConsentOp.class, "checkConsentValid", "Consent is not created by caller TPP: " + consent.getConsentId());

      throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Consent is not created by caller TPP: " + consent.getConsentId());
    }

    if (!consent.getConsentStatus().equalsIgnoreCase(ConsentStatuses.VALID)) {
      LogManager.trace(ConsentOp.class, "checkConsentValid", "Consent is not valid: " + consent.getConsentId());

      throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Consent is not valid!");
    }

    consent.checkOp();
  }


  @Override
  public String toString() {
    return "ConsentOp{" +
            "dbId=" + dbId +
            ", recurringIndicator=" + recurringIndicator +
            ", validUntil=" + validUntil +
            ", frequencyPerDay=" + frequencyPerDay +
            ", consentStatus='" + consentStatus + '\'' +
            ", consentId='" + consentId + '\'' +
            ", combinedServiceIndicator=" + combinedServiceIndicator +
            '}';
  }

  public boolean isValidUntil() {
    int c = Util.compareOnlyDatePart(validUntil, new Date());

    if (c >= 0) {
      return true;
    } else {
      return false;
    }

  }


}
