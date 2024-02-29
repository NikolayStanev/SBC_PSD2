package com.sbc.psd2.controller;


import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;

import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.rest.BackendJAXRSApp;
import com.sbc.psd2.rest.ErrorInfo;
import com.sbc.psd2.rest.Headers;
import com.sbc.psd2.rest.util.Util;
import com.sbc.util.eidas.EIDASCheck;
import com.sbc.util.eidas.EIDASInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.routing.Filter;
import org.restlet.util.Series;

//import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;



public class UserFilter extends Filter {
  protected static InheritableThreadLocal<Headers> threadLocalHeaders = new InheritableThreadLocal<Headers>();
  protected static InheritableThreadLocal<EIDASInfo> threadLocalEIDASInfo = new InheritableThreadLocal<>();
  protected static InheritableThreadLocal<UserInfo> threadLocalUser = new InheritableThreadLocal<>();


  @Override
  protected int beforeHandle(Request request, Response response) {
    try {
      EIDASInfo eidasInfo = checkEIDAS(request);
      threadLocalEIDASInfo.set(eidasInfo);

      Series<Header> requestHeaders = (Series<Header>) request.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
      //String authTokenGUID = requestHeaders.getValues(AUTH_HEADER_NAME);
      checkRequestHeaders(requestHeaders);

      Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);

      if (responseHeaders == null) {
        responseHeaders = new Series(Header.class);
        response.getAttributes().put("org.restlet.http.headers", responseHeaders);
      }

      Headers headers = new Headers(requestHeaders, responseHeaders);

      threadLocalHeaders.set(headers);

      String ip = getIP(request);
      checkIP(ip);

      //String authToken = Util.getAUTH2Token(request);
      //checkToken(authToken, eidasInfo.getThumbprint());
      UserInfo userInfo = new UserInfo();

      String psuID = Headers.getHeaderValue(requestHeaders, Headers.HEADER_PSU_ID);

      userInfo.setSessionID(psuID);
      userInfo.setCallerIP(ip);
      threadLocalUser.set(userInfo);

      Reference reference = request.getOriginalRef();
      String path = reference.getPath();

      if (!AppConfig.getInstance().isTestMode() && (path.contains("payments") || path.contains("consents"))) {
        checkUserIDToken();
      }

    } catch (Exception e) {
      errorHandling(response, e);
      return SKIP;
    }

    return CONTINUE;
  }

  @Override
  protected int doHandle(Request request, Response response) {
    try {
      //if (response.getStatus().getCode() < 400) {
        int res = super.doHandle(request, response);

        errorHandling(response);

        return res;
      //}

      //return CONTINUE;

    } catch (Throwable e) {
      LogManager.trace(getClass(), e.getClass().getName());
      //threadLocalHeaders.remove();

      errorHandling(response, e);

      return CONTINUE;
    }
  }

  @Override
  protected void afterHandle(Request request, Response response) {

    threadLocalHeaders.remove();
    threadLocalEIDASInfo.remove();
    threadLocalUser.remove();

  }

  public static Headers getHeaders() {
    return threadLocalHeaders.get();
  }

  public static EIDASInfo getEIDASInfo() {
    return threadLocalEIDASInfo.get();
  }

  public static UserInfo getUserInfo() {
    return threadLocalUser.get();
  }

  public static void errorHandling(Response response) {
    int responseStatusCode = response.getStatus().getCode();
    String responsePhrase = response.getStatus().getReasonPhrase();
    //Representation entity = response.getEntity();

    if (responseStatusCode >= 400) {
//      if (entity != null) {
//        try {
//          System.err.println(entity.getText());
//        } catch (Exception e) {
//          LogManager.log(getClass(), e);
//        }
//
//
//      }
      Representation rep = new JacksonRepresentation(MediaType.APPLICATION_JSON, new ErrorInfo(responseStatusCode, responsePhrase));
      response.setEntity(rep);

      Status newStatus = new Status(responseStatusCode, responsePhrase);
      response.setStatus(newStatus);

      //throw new ResourceException(responseStatusCode, response.getStatus().getReasonPhrase());
    }
  }

  public static void errorHandling(Response response, Throwable t) {
    int responseStatusCode = 500;
    String responsePhrase = "Internal Server Error!";

    if (t instanceof ApplicationException) {
      ApplicationException ex = (ApplicationException)t;

      responseStatusCode = ex.getStatus().getCode();
      responsePhrase = ex.getStatus().getReasonPhrase();
    }


    Representation rep = new JacksonRepresentation(MediaType.APPLICATION_JSON, new ErrorInfo(responseStatusCode, responsePhrase));
    response.setEntity(rep);

    Status newStatus = new Status(responseStatusCode, responsePhrase);
    response.setStatus(newStatus);
  }

  public static String getIP(Request _request) {
    HttpServletRequest request = ServletUtils.getRequest(_request);

    String ipAddress = request.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
      ipAddress = request.getRemoteAddr();
    }

    return ipAddress;
  }

  protected EIDASInfo checkEIDAS(Request request) throws ApplicationException {
    BackendJAXRSApp app = (BackendJAXRSApp) BackendJAXRSApp.getCurrent();

    if (app.isTestMode()) {
      return EIDASInfo.dummyInfo;
    }

    HttpServletRequest req = ServletUtils.getRequest(request);

    X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

    EIDASInfo info = EIDASCheck.check(certs);

    // check op vs roles in eidas
    String op = Util.getPSD2Op(request);
    if (!info.getRoles().contains(op)) {
      throw new ApplicationException(ApplicationException.SERVICE_INVALID, "No role for this operation in eIDAS roles!");
    }


    return info;
  }

  public static void checkUserIDToken() throws ApplicationException {
    String sessionID = threadLocalUser.get().getSessionID();

    if (sessionID == null) {
      throw new ApplicationException(ApplicationException.TOKEN_INVALID, "Not valid PSU_ID/token presented!");
    }

    EIDASInfo eidas = threadLocalEIDASInfo.get();

    IdentityManagementCommunicator idm = AbstractCommunicatorFactory.getInstance().getIdentityManagementCommunicator();

    UserInfo localInfo = idm.getUserByToken(sessionID, eidas.getThumbprint());
    if (localInfo != null) {
      threadLocalUser.set(localInfo);
    }
  }

  protected void checkRequestHeaders(Series<Header> headers) throws ApplicationException {
    String headerVal = Headers.getHeaderValue(headers, Headers.HEADER_X_REQUEST_ID);

    if (headerVal == null || headerVal.trim().equals("")) {
      throw new ApplicationException(ApplicationException.FORMAT_ERROR, "No X_REQUEST_ID header presented!");
    }
  }

  protected void checkIP(String ip) throws ApplicationException {
    // todo: implement it
  }
}
