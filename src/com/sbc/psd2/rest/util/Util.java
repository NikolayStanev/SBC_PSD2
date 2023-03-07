package com.sbc.psd2.rest.util;

import com.sbc.psd2.rest.Headers;
import com.sbc.util.eidas.EIDASInfo;
import org.restlet.Request;
import org.restlet.data.Header;
import org.restlet.data.Reference;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.util.Series;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 20-2-12
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class Util {
  public static String getPSD2Op(Request request) {
    Reference requestRef = request.getResourceRef();
    String path = requestRef.getPath();

    String op = "OP_UNKNOWN";

    if (path.contains("/psd2/v1/accounts/")) {
      op = EIDASInfo.ROLE_AI;
    }

    if (path.contains("/psd2/v1/consents/")) {
      op = EIDASInfo.ROLE_AI;
    }

    if (path.contains("/psd2/v1/bgn-credit-transfers/")) {
      op = EIDASInfo.ROLE_PI;
    }

    if (path.contains("/psd2/v1/funds-confirmations/")) {
      op = EIDASInfo.ROLE_IC;
    }

    return op;
  }

  public static String getAUTH2Token(Request request) {
    String auth = null;

    Series<Header> requestHeaders = (Series<Header>) request.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);

    String headerVal = Headers.getHeaderValue(requestHeaders, Headers.HEADER_AUTHORIZATION);
    if (headerVal != null) {
      headerVal = headerVal.trim();
      String[] pair = headerVal.split(" ");

      if (pair.length == 2) {
        if (pair[0].equalsIgnoreCase("Bearer")) {
          auth = pair[1];
        }
      }
    }

    return auth;
  }


  public static String doPostSync(final String urlToRead, final String content, String contentType) throws IOException {
    final String charset = "UTF-8";
    // Create the connection
    HttpURLConnection connection = (HttpURLConnection) new URL(urlToRead).openConnection();
    // setDoOutput(true) implicitly set's the request type to POST
    connection.setDoOutput(true);
    connection.setRequestProperty("Accept-Charset", charset);
    //connection.setRequestProperty("Content-type", "application/json");
    connection.setRequestProperty("Content-Type", contentType);//"text/xml");

    // Write to the connection
    OutputStream output = connection.getOutputStream();
    output.write(content.getBytes(charset));
    output.close();

    // Check the error stream first, if this is null then there have been no issues with the request
    InputStream inputStream = connection.getErrorStream();
    if (inputStream == null)
      inputStream = connection.getInputStream();

    // Read everything from our stream
    BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream, charset));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = responseReader.readLine()) != null) {
      response.append(inputLine);
    }
    responseReader.close();

    return response.toString();
  }
}
