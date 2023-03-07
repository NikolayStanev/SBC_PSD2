package com.sbc.psd2.data.consent;

import com.sbc.psd2.data.rest.links.Link;

public class GetConsentResponseLink extends Link {

    private String iban;

    public GetConsentResponseLink(String iban, String href) {
        super(href);
        this.iban = iban;
    }

  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }
}
