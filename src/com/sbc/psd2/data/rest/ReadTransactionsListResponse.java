package com.sbc.psd2.data.rest;

import com.sbc.psd2.data.rest.links.TransactionLinks;

public class ReadTransactionsListResponse {

    private AccountReference account;
    private AccountReport transactions;
    private TransactionLinks _links;

    public ReadTransactionsListResponse() {
    }

    public ReadTransactionsListResponse(AccountReference account, AccountReport transactions, TransactionLinks _links) {
        this.account = account;
        this.transactions = transactions;
        this._links = _links;
    }

    public AccountReference getAccount() {
        return account;
    }

    public void setAccount(AccountReference account) {
        this.account = account;
    }

    public AccountReport getTransactions() {
        return transactions;
    }

    public void setTransactions(AccountReport transactions) {
        this.transactions = transactions;
    }

    public TransactionLinks get_links() {
        return _links;
    }

    public void set_links(TransactionLinks _links) {
        this._links = _links;
    }

    public void set_links() {
    }
}
