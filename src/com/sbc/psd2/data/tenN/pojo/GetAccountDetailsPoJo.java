package com.sbc.psd2.data.tenN.pojo;

public class GetAccountDetailsPoJo {

    public String ibanNumber;


    public GetAccountDetailsPoJo(String ibanNumber) {
        this.ibanNumber = ibanNumber;
    }

    public String getIbanNumber() {
        return ibanNumber;
    }

    public void setIbanNumber(String ibanNumber) {
        this.ibanNumber = ibanNumber;
    }
}
