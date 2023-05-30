package com.sbc.psd2.controller.impl.tenN;

import com.sbc.psd2.controller.UserFilter;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.rest.Headers;
import com.sbc.util.eidas.EIDASInfo;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.util.Series;


public class TenNUserFilter extends UserFilter {

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

        } catch (Exception e) {
            errorHandling(response, e);
            return SKIP;
        }

        return CONTINUE;
    }

}
