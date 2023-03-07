package com.sbc.psd2.data.consent;

import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.rest.AccountAccess;
import com.sbc.psd2.data.rest.AccountReference;
import com.sbc.psd2.data.rest.IBAN;
import com.sbc.psd2.data.rest.links.Link;

import java.util.ArrayList;
import java.util.Date;

public class GetConsentResponse {

    private AccountAccess access;
    private Boolean recurringIndicator;
    private Date validUntil;
    private int frequencyPerDay;
    private Boolean combinedServiceIndicator;

    private GetConsentResponseLinks _links;

  public GetConsentResponse(AccountAccess access, Boolean recurringIndicator, Date validUntil, int frequencyPerDay, Boolean combinedServiceIndicator, GetConsentResponseLinks _links) {
        this.access = access;
        this.recurringIndicator = recurringIndicator;
        this.validUntil = validUntil;
        this.frequencyPerDay = frequencyPerDay;
        this.combinedServiceIndicator = combinedServiceIndicator;
        this._links = _links;
    }

    public static GetConsentResponse buildGetConsentResponse(ConsentOp op) {
      ArrayList<AccountReference> balances = new ArrayList<>();
      ArrayList<AccountReference> transactions = new ArrayList<>();
      ArrayList<AccountReference> accounts = new ArrayList<>();

      ArrayList<GetConsentResponseLink> linkBalances = new ArrayList<>();
      ArrayList<GetConsentResponseLink> linkTransactions = new ArrayList<>();
      ArrayList<GetConsentResponseLink> linkAccounts = new ArrayList<>();

      Link accountLink = new Link("/v1/accounts");
      String iban = "";
      for(Account account : op.getAccountMap().values()) {
          iban = account.getIban();

        if (account.getWithBalances()){
          AccountReference accountReference = new AccountReference(iban, account.getCurrency());
          balances.add(accountReference);

          GetConsentResponseLink link  = new GetConsentResponseLink(account.getIban(), "/v1/accounts/" + account.getAccountId()+"/balances");
          linkBalances.add(link);
        }
        if (account.getWithTransactions()){

          AccountReference accountReference = new AccountReference(iban, account.getCurrency());
          transactions.add(accountReference);

          GetConsentResponseLink link  = new GetConsentResponseLink(account.getIban(), "/v1/accounts/" + account.getAccountId()+"/transactions");
          linkTransactions.add(link);
        }
        if (account.getWithDetails()){

          AccountReference accountReference = new AccountReference(iban, account.getCurrency());
          accounts.add(accountReference);

          GetConsentResponseLink link  = new GetConsentResponseLink(account.getIban(), "/v1/accounts/" + account.getAccountId());
          linkAccounts.add(link);
        }
      }

      AccountAccess access = new AccountAccess(accounts, balances, transactions);
      GetConsentResponseLinks links = new GetConsentResponseLinks(accountLink, linkBalances, linkTransactions, linkAccounts);

      GetConsentResponse response = new GetConsentResponse(access, op.getRecurringIndicator(), op.getValidUntil(),
              op.getFrequencyPerDay(), op.getCombinedServiceIndicator(), links);

      return response;
    }

    public AccountAccess getAccess() {
        return access;
    }

    public void setAccess(AccountAccess access) {
        this.access = access;
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

    public Boolean getCombinedServiceIndicator() {
        return combinedServiceIndicator;
    }

    public void setCombinedServiceIndicator(Boolean combinedServiceIndicator) {
        this.combinedServiceIndicator = combinedServiceIndicator;
    }

  public GetConsentResponseLinks get_links() {
    return _links;
  }

  public void set_links(GetConsentResponseLinks _links) {
    this._links = _links;
  }
}
