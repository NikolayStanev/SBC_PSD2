package com.sbc.psd2.data.account;

import com.sbc.psd2.data.rest.links.Link;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-22
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class AccountLinks {
  private Link self;
  private Link balances;
  private Link transactions;

  public AccountLinks(Link self, Link balances, Link transactions) {
    this.self = self;
    this.balances = balances;
    this.transactions = transactions;
  }

  public Link getSelf() {
    return self;
  }

  public void setSelf(Link self) {
    this.self = self;
  }

  public Link getBalances() {
    return balances;
  }

  public void setBalances(Link balances) {
    this.balances = balances;
  }

  public Link getTransactions() {
    return transactions;
  }

  public void setTransactions(Link transactions) {
    this.transactions = transactions;
  }

  public static AccountLinks buildLinks(Account account) {
    Link self = null;
    Link balances = null;
    Link trans = null;

    String resourceId = account.getAccountId();

    if (account.getWithDetails()) {
      String selfHref = "/v1/accounts/" + resourceId;

      self = new Link(selfHref);
    }

    if (account.getWithBalances()) {
      String balancesHref = "/v1/accounts/" + resourceId + "/balances";

      balances = new Link(balancesHref);
    }

    if (account.getWithTransactions()) {
      String transHref = "/v1/accounts/" + resourceId + "/transactions";

      trans = new Link(transHref);
    }

    return new AccountLinks(self, balances, trans);
  }
}
