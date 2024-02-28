package com.sbc.psd2.rest.resources;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.controller.*;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.creditTransfer.*;
import com.sbc.psd2.data.statuses.TransactionStatuses;
import com.sbc.psd2.AuthorizeResponse;
import com.sbc.psd2.data.creditTransfer.dao.BGNCreditTransferOpDAO;
import com.sbc.psd2.data.rest.PsuData;
import com.sbc.psd2.rest.Headers;

import javax.ws.rs.*;

@Path("/payments")
public class CreditTransferResource {

  @POST
  @Path("/domestic-credit-transfers-bgn")
  @Produces("application/json")
  public BGNCreditTransferResponse doBGNCreditTransfer(BGNCreditTransferRequest request) throws ApplicationException {
    LogManager.trace(getClass(), "doBGNCreditTransfer");

    if (request == null) {
      throw new ApplicationException(ApplicationException.FORMAT_ERROR, "No data!");
    }

    LogManager.trace(getClass(), "doBGNCreditTransfer", request.toString());

    Headers headers = UserFilter.getHeaders();

    // Should consent to be checked at all?
    // If user A creates consent X, user B could create payment initiation on debit account listed in consent X
    // after that user A should confirm with OTP/SMS the payment initiated from user B
    // The TPP app should notify user A there is pending transaction which should be authorized
    // TPP app knows IBANs with valid consents linked to owners and it is easy to notify owner when payment initiation
    // is created by another user on IBAN not owned by him
    // In other words when payment initiation is created TPP app should link payment initiation ID to the user which is owner of the debit account

//    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
//    String consentID = null;//headers.getRequestHeaderValue(Headers.HEADER_CONSENT_ID);
//    if (consentID != null) {
//      ConsentOp consent = ConsentOpDAO.getConsentByConsentID(consentID, tppID);
//
//      if (consent != null && consent.getCombinedServiceIndicator()) {
//        ConsentOp.checkConsentValid(consent);
//
//        Account account = AccountDAO.getAccountByIBAN(request.getDebtorAccount().getIban().getIban(), consent.getDbId());
//
//        if (account == null) {
//          throw new ApplicationException(ApplicationException.FORMAT_ERROR, "No account for that consent!");
//        }
//
//        synchronized (account.getAccountId().intern()) {
//          if (account.updateTriesVsConsent(consent)) {
//            AccountDAO.updateAccount(account);
//          } else {
//            throw new ApplicationException(ApplicationException.ACCESS_EXCEEDED, "Maximum access quota is reached!");
//          }
//        }
//      } else {
//        throw new ApplicationException(ApplicationException.CONSENT_INVALID, "Consent is not found or it has combinedServiceIndicator = false!");
//      }
//    }

    // add values from headers to request data
    Headers.enrichRequest(headers.getRequestHeaders(), request);

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    // check accounts and user relation
    // it will throw ApplicationException in case of invalid op params
    // which will result in appropriate HTTP response code and message
    UserInfo userInfo = UserFilter.getUserInfo();

    BGNCreditTransferOp op = BGNCreditTransferOp.buildBGNCreditTransferOp(request, userInfo);

    CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();

    csCommunicator.validateIBANs(op.getDebtorAccount().getIban(), userInfo);

    // save into db info about request and response
    BGNCreditTransferOpDAO.createTransferOp(op);

    // should do it in this way - without OTP authorization
    // in case of combined service indicator set to true and provided valid consent ID
    // now it is commented - fo future releases
    //if (consentID != null)  {
    //  bookTransaction(op);
    //}

    AppConfig appConfig = AppConfig.getInstance();

    if (appConfig.isImmediateTransaction()) {
      TaskExecutor.INSTANCE.bookTransactionAsync(op);
    }

    // build response
    BGNCreditTransferResponse response = BGNCreditTransferResponse.buildResponse(op);

    return response;
  }


  @GET
  @Path("/domestic-credit-transfers-bgn/{paymentId}")
  @Produces("application/json")
  public BGNCreditTransferInfoResponse getBGNCreditTransfer(@PathParam("paymentId") String paymentId) throws ApplicationException {
    LogManager.trace(getClass(), "getBGNCreditTransfer", paymentId);

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    // get info about transaction
    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    BGNCreditTransferOp op = BGNCreditTransferOp.getAndCheckOp(paymentId, tppID);

    BGNCreditTransferInfoResponse response = BGNCreditTransferInfoResponse.buildFromOp(op);

    return response;
  }

  @GET
  @Path("/domestic-credit-transfers-bgn/{paymentId}/status")
  @Produces("application/json")
  public BGNCreditTransferStatusResponse getBGNCreditTransferStatus(@PathParam("paymentId") String paymentId) throws ApplicationException {
    LogManager.trace(getClass(), "getBGNCreditTransferStatus", paymentId);

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    // get info about transaction
    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    BGNCreditTransferOp op = BGNCreditTransferOp.getAndCheckOp(paymentId, tppID);

    String psd2Status = op.getTransactionStatus();

    String extRefID = op.getExtRefID();

    if (extRefID != null) {
      CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();

      psd2Status = csCommunicator.getTransactionStatus(op);

      op.setTransactionStatus(psd2Status);
      BGNCreditTransferOpDAO.update(op);
    }
    //String status = op.getTransactionStatus();

    BGNCreditTransferStatusResponse response = new BGNCreditTransferStatusResponse(psd2Status);

    return response;
  }


  @PUT
  @Path("/domestic-credit-transfers-bgn/{paymentId}/reject")
  @Produces("application/json")
  public void rejectBGNCreditTransfer(@PathParam("paymentId") String paymentId) throws ApplicationException {
    LogManager.trace(getClass(), "rejectBGNCreditTransfer", paymentId);

    UserFilter.checkUserIDToken();

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    // get info about transaction
    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    BGNCreditTransferOp op = BGNCreditTransferOpDAO.getOpByPaymentID(paymentId, tppID);

    if (op != null) {
      if (op.getTransactionStatus() == TransactionStatuses.RECEIVED) {
        op.setTransactionStatus(TransactionStatuses.REJECTED);
        BGNCreditTransferOpDAO.update(op);
      }
    } else {
      throw new ApplicationException(ApplicationException.RESOURCE_UNKNOWN_PATH, "There is no transaction with that ID!");
    }
  }


  @PUT
  @Path("/domestic-credit-transfers-bgn/{paymentId}/authorisations/push")
  @Produces("application/json")
  public void doBGNCreditTransferAuthorizePush(@PathParam("paymentId") String paymentId) throws ApplicationException {
    LogManager.trace(getClass(), "doBGNCreditTransferAuthorizePush", paymentId);

    UserFilter.checkUserIDToken();

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    // get payment info
    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    BGNCreditTransferOp op = BGNCreditTransferOp.getAndCheckOp(paymentId, tppID);

//      AccountDetails debtorAccountDetails = op.getDebtorAccount();
//      IBAN iban = debtorAccountDetails.getIban();
//
//      String token = DummyIdentityManagementCommunicator.getTokenByIban(iban.getIban());
//      if (token == null) {
//        throw new ApplicationException(ApplicationException.PSU_CREDENTIALS_INVALID, "Token not found!");
//      }

    SCACommunicator scaCommunicator = AbstractCommunicatorFactory.getInstance().getScaCommunicator();

    scaCommunicator.generateOTP(op);
  }


  @PUT
  @Path("/domestic-credit-transfers-bgn/{paymentId}/authorisations/check")
  @Produces("application/json")
  public AuthorizeResponse doBGNCreditTransferAuthorizeCheck(@PathParam("paymentId") String paymentId, PsuData authData) throws ApplicationException {
    LogManager.trace(getClass(), "doBGNCreditTransferAuthorizeCheck");

    UserFilter.checkUserIDToken();

    if (authData == null) {
      throw new ApplicationException(ApplicationException.TOKEN_INVALID, "No auth data!");
    }

    Headers headers = UserFilter.getHeaders();

    // copy X_REQUEST_ID header into response headers
    Headers.copyRequestHeader(Headers.HEADER_X_REQUEST_ID, headers);

    // set SCA_APPROACH header into response headers
    headers.getResponseHeaders().add(Headers.HEADER_SCA_APPROACH, Headers.getValueScaApproach());

    // get payment info
    String tppID = UserFilter.getEIDASInfo().getTppAuthNumber();
    BGNCreditTransferOp op = BGNCreditTransferOp.getAndCheckOp(paymentId, tppID);

    String psd2Status;

    String otp = authData.getScaAuthenticationData();
    UserInfo info = UserFilter.getUserInfo();
    info.setOtp(otp);

    SCACommunicator scaCommunicator = AbstractCommunicatorFactory.getInstance().getScaCommunicator();
    boolean result = scaCommunicator.checkOTP(op, otp);
    if (result) {
      // book transaction
      psd2Status = TaskExecutor.bookTransaction(op);
    } else {
      LogManager.trace(getClass(), "doBGNCreditTransferAuthorizeCheck()", "Result of otp check is false " + op.getPaymentId());

      throw new ApplicationException(ApplicationException.PSU_CREDENTIALS_INVALID, "Bad OTP!");
    }

    return new AuthorizeResponse(psd2Status);

  }



}
