package com.sbc.psd2.rest.resources;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.controller.*;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.statuses.ConsentStatuses;
import com.sbc.psd2.AuthorizeResponse;
import com.sbc.psd2.data.consent.*;
import com.sbc.psd2.data.consent.dao.ConsentOpDAO;
import com.sbc.psd2.data.rest.PsuData;
import com.sbc.psd2.rest.Headers;

import javax.ws.rs.*;

@Path("/consents")
public class ConsentResource {

  @POST
  @Path("/")
  @Consumes("application/json")
  @Produces("application/json")
  public ConsentResponse createConsent(ConsentRequest request) throws ApplicationException {
    if (request == null) {
      throw new ApplicationException(ApplicationException.FORMAT_ERROR, "No data!");
    }

    //UserFilter.checkUserIDToken();

    LogManager.trace(getClass(), "createConsent", request.toString());

    Headers headers = UserFilter.getHeaders();

    Headers.enrichRequest(headers.getRequestHeaders(), request);

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // add SCA approach header
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    // save into db info about request and response
    ConsentOp op = ConsentOp.buildConsentOp(request);
    UserInfo userInfo = UserFilter.getUserInfo();

    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
    csCommunicator.validateIBANs(op.getAccountMap(), userInfo);

    ConsentOpDAO.createConsent(op);

    AppConfig appConfig = AppConfig.getInstance();

    if (appConfig.isImmediateTransaction()) {
      TaskExecutor.INSTANCE.startConsentAuth(op);
    }

    // build response from request
    ConsentResponse response = ConsentResponse.buildResponse(op);

    return response;
  }

  @GET
  @Path("/{consentId}/status")
  @Produces("application/json")
  public ConsentStatusResponse getConsentStatus(@PathParam("consentId") String consentId) throws ApplicationException {
    LogManager.trace(getClass(), "getConsentStatus", consentId);

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    ConsentOp op = ConsentOpDAO.getConsentByConsentID(consentId, tppID);

    if (op != null) {
      ConsentStatusResponse response = new ConsentStatusResponse(op.getConsentStatus());

      return response;
    } else {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "There is no consent with that ID!");
    }


  }

  @GET
  @Path("/{consentId}")
  @Produces("application/json")
  public GetConsentResponse getConsent(@PathParam("consentId") String consentId) throws ApplicationException {
    LogManager.trace(getClass(), "getConsent", consentId);

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    // get info about transaction
    ConsentOp op = ConsentOpDAO.getConsentByConsentID(consentId, tppID);

    if (op != null) {
      op.checkOp();

      GetConsentResponse response = GetConsentResponse.buildGetConsentResponse(op);

      return response;
    } else {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "There is no consent with that ID!");
    }
  }

  @PUT
  @Path("/{consentId}/authorisations/push")
  @Produces("application/json")
  public void consentAuthorizePush(@PathParam("consentId") String consentId) throws ApplicationException {
    LogManager.trace(getClass(), "consentAuthorizePush", consentId);

    UserFilter.checkUserIDToken();

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    // get payment info
    ConsentOp op = ConsentOpDAO.getConsentByConsentID(consentId, tppID);


    if (op != null) {
      op.checkOp();

      SCACommunicator scaCommunicator = AbstractCommunicatorFactory.getInstance().getScaCommunicator();

      scaCommunicator.generateOTP(op);
    } else {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "Resource not found!");
    }
  }

  @PUT
  @Path("/{consentId}/authorisations/check")
  @Produces("application/json")
  public AuthorizeResponse consentAuthorizeCheck(@PathParam("consentId") String consentId, PsuData authData) throws ApplicationException {
    LogManager.trace(getClass(), "consentAuthorizeCheck", consentId);

    UserFilter.checkUserIDToken();

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    // get payment info
    ConsentOp op = ConsentOpDAO.getConsentByConsentID(consentId, tppID);

    String otp = authData.getScaAuthenticationData();
    UserInfo info = UserFilter.getUserInfo();
    info.setOtp(otp);

    if (op != null) {
      op.checkOp();

      SCACommunicator scaCommunicator = AbstractCommunicatorFactory.getInstance().getScaCommunicator();

      boolean result = scaCommunicator.checkOTP(op, otp);
      if (result) {
        ConsentOpDAO.updateConsentStatus(op.getDbId(), ConsentStatuses.VALID);
        return new AuthorizeResponse(ConsentStatuses.VALID);

      } else {
        throw new ApplicationException(ApplicationException.PSU_CREDENTIALS_INVALID, "Bad OTP!");
      }
    } else {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "There is no consent with that ID!");
    }
  }

  @DELETE
  @Path("/{consentId}")
  @Produces("application/json")
  public void deleteConsent(@PathParam("consentId") String consentId) throws ApplicationException {
    LogManager.trace(getClass(), "deleteConsent", consentId);

    UserFilter.checkUserIDToken();

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    //ConsentOpDAO.deleteConsent(consentId, tppID);
    ConsentOp op = ConsentOpDAO.getConsentByConsentID(consentId, tppID);

    if (op != null) {
      ConsentOpDAO.updateConsentStatus(op.getDbId(), ConsentStatuses.REVOKED_BY_PSU);
    } else {
      throw new ApplicationException(ApplicationException.CONSENT_UNKNOWN, "No consent found with that id!");
    }

  }

}
