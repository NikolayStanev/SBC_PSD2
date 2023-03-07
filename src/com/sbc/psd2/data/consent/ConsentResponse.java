package com.sbc.psd2.data.consent;

public class ConsentResponse {
  private String consentStatus;
  private String consentId;
  private ConsentResponseLinks _links;

  public ConsentResponse() {

  }

  public ConsentResponse(String consentStatus, String consentId, ConsentResponseLinks _links) {
    this.consentStatus = consentStatus;
    this.consentId = consentId;
    this._links = _links;
  }

  public static ConsentResponse buildResponse(ConsentOp op) {
    String consentStatus = op.getConsentStatus();
    String consentId = op.getConsentId();


    String self = "/v1/consents/" + consentId;
    String status = "/v1/consents/" + consentId + "/status";
    String auth = "/v1/consents/" + consentId + "/authorisations";

    ConsentResponseLinks _links = new ConsentResponseLinks(self, status, auth);

    ConsentResponse consentResponse = new ConsentResponse(consentStatus, consentId, _links);

    return consentResponse;
  }

  public String getConsentStatus() {
    return consentStatus;
  }

  public void setConsentStatus(String consentStatus) {
    this.consentStatus = consentStatus;
  }

  public String getConsentId() {
    return consentId;
  }

  public void setConsentId(String consentId) {
    this.consentId = consentId;
  }

  public ConsentResponseLinks get_links() {
    return _links;
  }

  public void set_links(ConsentResponseLinks _links) {
    this._links = _links;
  }



}
