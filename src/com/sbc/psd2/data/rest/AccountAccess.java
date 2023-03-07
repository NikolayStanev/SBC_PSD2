package com.sbc.psd2.data.rest;

import java.util.ArrayList;

public class AccountAccess {

    private ArrayList<AccountReference> accounts;
    private ArrayList<AccountReference> balances;
    private ArrayList<AccountReference> transactions;

    public AccountAccess() {
    }

    public AccountAccess(ArrayList<AccountReference> accounts, ArrayList<AccountReference> balances, ArrayList<AccountReference> transactions) {
        this.accounts = accounts;
        this.balances = balances;
        this.transactions = transactions;
    }

    public ArrayList<AccountReference> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<AccountReference> accounts) {
        this.accounts = accounts;
    }

    public ArrayList<AccountReference> getBalances() {
        return balances;
    }

    public void setBalances(ArrayList<AccountReference> balances) {
        this.balances = balances;
    }

    public ArrayList<AccountReference> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<AccountReference> transactions) {
        this.transactions = transactions;
    }
}
