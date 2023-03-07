package com.sbc.psd2.data.consent;

import com.sbc.psd2.data.rest.links.Link;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-16
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */
public class GetConsentResponseLinks {
  // links for access accounts in getConsent
  private Link account;
  private ArrayList<GetConsentResponseLink> balances;
  private ArrayList<GetConsentResponseLink> transactions;
  private ArrayList<GetConsentResponseLink> accountDetails;

  public GetConsentResponseLinks() {
  }

  public GetConsentResponseLinks(Link account, ArrayList<GetConsentResponseLink> balances, ArrayList<GetConsentResponseLink> transactions, ArrayList<GetConsentResponseLink> accountDetails) {
    this.account = account;
    this.balances = balances;
    this.transactions = transactions;
    this.accountDetails = accountDetails;
  }

  public Link getAccount() {
    return account;
  }

  public void setAccount(Link account) {
    this.account = account;
  }

  public ArrayList<GetConsentResponseLink> getBalances() {
    return balances;
  }

  public void setBalances(ArrayList<GetConsentResponseLink> balances) {
    this.balances = balances;
  }

  public ArrayList<GetConsentResponseLink> getTransactions() {
    return transactions;
  }

  public void setTransactions(ArrayList<GetConsentResponseLink> transactions) {
    this.transactions = transactions;
  }

  public ArrayList<GetConsentResponseLink> getAccountDetails() {
    return accountDetails;
  }

  public void setAccountDetails(ArrayList<GetConsentResponseLink> accountDetails) {
    this.accountDetails = accountDetails;
  }
}
