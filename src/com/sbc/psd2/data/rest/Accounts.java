package com.sbc.psd2.data.rest;

import java.util.ArrayList;

public class Accounts {

  private ArrayList<AccountDetails> accounts;

  public Accounts() {
    accounts = new ArrayList<>();
  }

  public Accounts(ArrayList<AccountDetails> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<AccountDetails> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<AccountDetails> accounts) {
        this.accounts = accounts;
    }

  public void addAccountDetails(AccountDetails info) {
    accounts.add(info);
  }
}
