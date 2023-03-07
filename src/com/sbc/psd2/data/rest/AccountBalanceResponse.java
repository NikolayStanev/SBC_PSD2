package com.sbc.psd2.data.rest;

import java.util.ArrayList;

public class AccountBalanceResponse {

    private AccountReference accountReference;
    private ArrayList<Balance> balances;

    public AccountBalanceResponse() {
    }

    public AccountBalanceResponse(AccountReference accountReference, ArrayList<Balance> balances) {
        this.accountReference = accountReference;
        this.balances = balances;
    }

    public AccountReference getAccountReference() {
        return accountReference;
    }

    public void setAccountReference(AccountReference accountReference) {
        this.accountReference = accountReference;
    }

    public ArrayList<Balance> getBalance() {
        return balances;
    }

    public void setBalance(ArrayList<Balance> balances) {
        this.balances = balances;
    }

    @Override
    public String toString() {
        return "AccountBalanceResponse{" +
                "accountReference='" + accountReference + '\'' +
                ", balance='" + balances +
                '}';
    }
}
