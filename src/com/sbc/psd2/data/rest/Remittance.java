package com.sbc.psd2.data.rest;

public class Remittance {

    private String reference;
    private String referenceType;
    private String referenceIssuer;

  public Remittance() {
  }

  public Remittance(String reference, String referenceType, String referenceIssuer) {
    this.reference = reference;
    this.referenceType = referenceType;
    this.referenceIssuer = referenceIssuer;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  public String getReferenceIssuer() {
    return referenceIssuer;
  }

  public void setReferenceIssuer(String referenceIssuer) {
    this.referenceIssuer = referenceIssuer;
  }
}
