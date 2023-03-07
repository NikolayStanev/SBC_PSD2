package com.sbc.psd2.rest.resources;


import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.data.fundsConfirmation.FundsConfirmationsRequest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/funds-confirmations")
public class FundsConfirmations {


    @POST
    @Path("/")
    @Produces("application/json")
    public boolean fundsConfirmations (FundsConfirmationsRequest fundsConfirmationsRequest) throws ApplicationException {
        LogManager.trace(getClass(), "getBalance");

        CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();

        boolean fundsAvailable = csCommunicator.confirmFunds(fundsConfirmationsRequest.getAccount().getIban(),
                fundsConfirmationsRequest.getInstructedAmount().getContent(),
                fundsConfirmationsRequest.getInstructedAmount().getCurrency());

        return fundsAvailable;
    }


}
