package com.sbc.psd2.rest.resources;


import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.consent.GetConsentResponse;
import com.sbc.psd2.data.consent.dao.ConsentOpDAO;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferInfoResponse;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/open")
public class OpenResource {


    @GET
    @Path("/{consentId}/{tppID}")
    @Produces("application/json")
    public GetConsentResponse getConsent(@PathParam("consentId") String consentId,@PathParam("tppID") String tppID) throws ApplicationException {
        LogManager.trace(getClass(), "getConsent", consentId);

        ConsentOp op = ConsentOpDAO.getConsentByConsentID(consentId, tppID);

        return GetConsentResponse.buildGetConsentResponse(op);
    }

    @GET
    @Path("/domestic-credit-transfers-bgn/{paymentId}//{tppID}")
    @Produces("application/json")
    public BGNCreditTransferInfoResponse getBGNCreditTransfer(@PathParam("paymentId") String paymentId,@PathParam("tppID") String tppID) throws ApplicationException {
        LogManager.trace(getClass(), "getBGNCreditTransfer", paymentId);

        BGNCreditTransferOp op = BGNCreditTransferOp.getAndCheckOp(paymentId, tppID);

        BGNCreditTransferInfoResponse response = BGNCreditTransferInfoResponse.buildFromOp(op);

        return response;
    }


}
