package com.sbc.psd2.data.account;

import com.sbc.common.util.Util;
import com.sbc.psd2.data.consent.ConsentOp;

import java.util.Date;

public class Account {

    private int id = -1;
    private String iban;
    private String accountId;
    private Boolean withTransactions;
    private Boolean withBalances;
    private Boolean withDetails;
    private int consentId;
    private String currency;
    private int tries;
    private Date currentDate;

    private String branch;


    // without branch
    public Account(int id, String iban, String accountId, Boolean withTransactions, Boolean withBalances,
                   Boolean withDetails, int consentId, String currency, int tries, Date currentDate) {
        this.id = id;
        this.iban = iban;
        this.accountId = accountId;
        this.withTransactions = withTransactions;
        this.withBalances = withBalances;
        this.withDetails = withDetails;
        this.consentId = consentId;
        this.currency = currency;
        this.tries = tries;
        this.currentDate = currentDate;
    }
    //with branch
    public Account(int id, String iban, String accountId, Boolean withTransactions, Boolean withBalances,
                   Boolean withDetails, int consentId, String currency, int tries, Date currentDate, String branch) {
        this.id = id;
        this.iban = iban;
        this.accountId = accountId;
        this.withTransactions = withTransactions;
        this.withBalances = withBalances;
        this.withDetails = withDetails;
        this.consentId = consentId;
        this.currency = currency;
        this.tries = tries;
        this.currentDate = currentDate;
        this.branch = branch;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Boolean getWithTransactions() {
        return withTransactions;
    }


    public void setWithTransactions(Boolean withTransactions) {
        this.withTransactions = withTransactions;
    }

    public Boolean getWithBalances() {
        return withBalances;

    }

    public void setWithBalances(Boolean withBalances) {
        this.withBalances = withBalances;
    }

    public Boolean getWithDetails() {
        return withDetails;
    }

    public void setWithDetails(Boolean withDetails) {
        this.withDetails = withDetails;
    }

    public int getConsentId() {
        return consentId;
    }

    public void setConsentId(int consentId) {
        this.consentId = consentId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public synchronized int getTries() {
        return tries;
    }

    public synchronized void setTries(int tries) {
        this.tries = tries;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public boolean updateTriesVsConsent(ConsentOp consent) {
      if (!consent.getRecurringIndicator() && tries == 1) {
        return false;
      }

      Date today = new Date();
      if (Util.compareOnlyDatePart(today, currentDate) > 0) {
        tries = 1;
        currentDate = today;
      } else {
        tries++;
      }

      if (tries > consent.getFrequencyPerDay()) {
        return false;
      }

      return true;
    }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;

    if (iban != null ? !iban.equals(account.iban) : account.iban != null) return false;
    if (currency != null ? !currency.equals(account.currency) : account.currency != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = iban != null ? iban.hashCode() : 0;
    result = 31 * result + (currency != null ? currency.hashCode() : 0);
    return result;
  }
  //    TODO: string override!
}
