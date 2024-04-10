package com.sbc.psd2.rest;

import com.sbc.common.util.Implementation;
import com.sbc.common.util.ScaApproach;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.UserFilter;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.data.PSD2RequestCommonData;
import org.restlet.data.Header;
import org.restlet.util.Series;

public class Headers {
  public static final String HEADER_X_REQUEST_ID = "X-Request-ID";
  public static final String HEADER_SCA_APPROACH = "ASPSP-SCA-Approach";
  public static final String HEADER_PSU_IP = "PSU-IP-Address";
  public static final String HEADER_PSU_ID = "PSU-ID";
  public static final String HEADER_CONSENT_ID = "Consent-ID";

  //Authorization: Bearer SlAV32hkKG
  public static final String HEADER_AUTHORIZATION = "Authorization";

  public static final String VALUE_SCA_APPROACH_EMBEDDED = "EMBEDDED";

  public static final String VALUE_SCA_APPROACH_REDIRECT = "REDIRECT";


  private Series<Header> requestHeaders;
  private Series<Header> responseHeaders;

  public Headers(Series<Header> requestHeaders, Series<Header> responseHeaders) {
    this.requestHeaders = requestHeaders;
    this.responseHeaders = responseHeaders;
  }

  public Series<Header> getRequestHeaders() {
    return requestHeaders;
  }

  public Series<Header> getResponseHeaders() {
    return responseHeaders;
  }

  public String getRequestHeaderValue(String headerName) {
    return getHeaderValue(requestHeaders, headerName);
  }

  public static String getHeaderValue(Series<Header> headers, String headerName) {
    Header header = headers.getFirst(headerName,true);
    if (header == null) {
      header = headers.getFirst(headerName.toLowerCase());
    }

    if (header == null) {
      return null;
    } else {
      return header.getValue();
    }

  }

  public static void enrichRequest(Series<Header> requestHeaders, PSD2RequestCommonData request) {
    request.setxRequestID(getHeaderValue(requestHeaders, HEADER_X_REQUEST_ID));
    request.setPsuIPAddress(getHeaderValue(requestHeaders, HEADER_PSU_IP));
    request.setPsuID(getHeaderValue(requestHeaders, HEADER_PSU_ID));
    request.setConsentID(getHeaderValue(requestHeaders, HEADER_CONSENT_ID));

    String ip = UserFilter.getUserInfo().getCallerIP();
    request.setIp(ip);
  }

  public static void copyRequestHeader(String headerName, Headers headers) {
    Series<Header> requestHeaders = headers.getRequestHeaders();
    Series<Header> responseHeaders = headers.getResponseHeaders();

    responseHeaders.add(headerName, getHeaderValue(requestHeaders, headerName));
  }

  public static String getValueScaApproach () {

    return AbstractCommunicatorFactory.getInstance().getValueScaApproach().toString();

  }
}
