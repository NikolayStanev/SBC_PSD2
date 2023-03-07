package com.sbc.psd2.data.rest.links;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-22
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class Links extends BaseLinks {
  //private String scaRedirect;

  private Link startAuthorisationWithPsuAuthentication;
  // link where to send uid and password to authenticate user
  // we will not provide link like that since user is authenticated somewhere outside us
  private Link updatePsuAuthentication;

  // link used to send OTP for transaction authorization
  private Link authoriseTransaction;

  private Link account;
  private Link balances;
  private Link transactions;


  public Links() {
  }


  public Link getUpdatePsuAuthentication() {
    return updatePsuAuthentication;
  }

  public void setUpdatePsuAuthentication(Link updatePsuAuthentication) {
    this.updatePsuAuthentication = updatePsuAuthentication;
  }

  public void setUpdatePsuAuthenticationString(String updatePsuAuthentication) {
    this.updatePsuAuthentication = new Link(updatePsuAuthentication);
  }

  public Link getAuthoriseTransaction() {
    return authoriseTransaction;
  }

  public void setAuthoriseTransaction(Link authoriseTransaction) {
    this.authoriseTransaction = authoriseTransaction;
  }

  public void setAuthoriseTransactionString(String authoriseTransaction) {
    this.authoriseTransaction = new Link(authoriseTransaction);
  }


  public Link getStartAuthorisationWithPsuAuthentication() {
    return startAuthorisationWithPsuAuthentication;
  }

  public void setStartAuthorisationWithPsuAuthentication(Link startAuthorisationWithPsuAuthentication) {
    this.startAuthorisationWithPsuAuthentication = startAuthorisationWithPsuAuthentication;
  }
  public void setStartAuthorisationWithPsuAuthenticationString(String startAuthorisationWithPsuAuthentication) {
    this.startAuthorisationWithPsuAuthentication = new Link(startAuthorisationWithPsuAuthentication);
  }

  public Link getAccount() {
    return account;
  }

  public void setAccount(Link account) {
    this.account = account;
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

  @Override
  public String toString() {
    return "Links{" +
            "updatePsuAuthentication=" + updatePsuAuthentication +
            ", authoriseTransaction=" + authoriseTransaction + '}';
  }
}
