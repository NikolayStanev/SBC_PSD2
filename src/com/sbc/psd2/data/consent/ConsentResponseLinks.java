package com.sbc.psd2.data.consent;

import com.sbc.psd2.data.rest.links.BaseLinks;
import com.sbc.psd2.data.rest.links.Link;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-22
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class ConsentResponseLinks extends BaseLinks {
  private Link startAuthorisationWithPsuAuthentication;

  public ConsentResponseLinks() {

  }

  public ConsentResponseLinks(String selfHref, String statusHref, String authHref) {
    super(selfHref, statusHref);

    startAuthorisationWithPsuAuthentication = new Link(authHref);
  }

  public Link getStartAuthorisationWithPsuAuthentication() {
    return startAuthorisationWithPsuAuthentication;
  }

  public void setStartAuthorisationWithPsuAuthentication(Link startAuthorisationWithPsuAuthentication) {
    this.startAuthorisationWithPsuAuthentication = startAuthorisationWithPsuAuthentication;
  }
}
