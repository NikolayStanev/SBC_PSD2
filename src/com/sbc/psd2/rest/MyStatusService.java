package com.sbc.psd2.rest;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;

public class MyStatusService extends StatusService {
//  public Status toStatus(Throwable throwable, Request request, Response response) {
//    Status status = super.toStatus(throwable, request, response);
//
//    return status;
//  }

    public Representation toRepresentation(Status status,
                                           Request request,
                                           Response response) {

        int responseStatusCode = status.getCode();
        String text = status.getReasonPhrase();
        Throwable t = status.getThrowable();

        if (t != null) {
            t.printStackTrace();
        }


        Representation rep = new JacksonRepresentation(MediaType.APPLICATION_JSON, new ErrorInfo(responseStatusCode, text));

        return rep;
    }
}
