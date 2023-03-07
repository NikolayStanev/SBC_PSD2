package com.sbc.psd2.data.rest.links;

public class TransactionLinks {

    private Link account;

    public TransactionLinks(Link account) {
        this.account = account;
    }

    public static TransactionLinks buildLinks (String accountId){

       Link link = new Link ("v1/accounts/" + accountId);

       return new TransactionLinks(link);
   }

    public Link getAccount() {
        return account;
    }

    public void setAccount(Link account) {
        this.account = account;
    }
}
