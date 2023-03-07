package com.sbc.psd2.data.tenN;

import java.util.ArrayList;

public class TenNCoreIndividual {

    public int individualId;
    public int merchantId;
    public ArrayList<TenNCoreAccount> accounts;


    public TenNCoreIndividual() {
    }

    public int getIndividualId() {
        return individualId;
    }

    public void setIndividualId(int individualId) {
        this.individualId = individualId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public ArrayList<TenNCoreAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<TenNCoreAccount> accounts) {
        this.accounts = accounts;
    }
}
