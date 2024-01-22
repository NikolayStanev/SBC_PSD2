package com.sbc.psd2.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.data.tenN.pojo.ErrorPojo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private final String GET = "GET";

    private final String POST = "POST";
    private URL url;

    private String contentType = "application/json";

    private HashMap<String, String> queryParams;

    private HashMap<String, String> pathParams;

    private HashMap<String, String> headers;


    private Object requestBody;

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public HttpClient(URL url, String contentType, Object requestBody) {
        this.url = url;
        this.contentType = contentType;
        this.requestBody = requestBody;
    }

    public HttpClient(URL url, String contentType, HashMap<String, String> headers) {
        this.url = url;
        this.contentType = contentType;
        this.headers = headers;
    }

    public HttpClient(URL url, String contentType, HashMap<String, String> pathParams, HashMap<String, String> headers, Object requestBody) {
        this.url = url;
        this.contentType = contentType;
        this.pathParams = pathParams;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpClient(URL url, String contentType, HashMap<String, String> queryParams, HashMap<String, String> pathParams, HashMap<String, String> headers, Object requestBody) {
        this.url = url;
        this.contentType = contentType;
        this.queryParams = queryParams;
        this.pathParams = pathParams;
        this.requestBody = requestBody;
        this.headers = headers;
    }

    public <T>T doPost (Class<T> c) {
        LogManager.trace(HttpClient.class, "doPost()", url.toString());

        HttpURLConnection connection = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String requestBodyS;
            if (requestBody instanceof String) {
                requestBodyS = (String) requestBody;
            }else {
                requestBodyS = objectMapper.writeValueAsString(requestBody);
                LogManager.trace(HttpClient.class, "RequestBody: ", requestBodyS);
            }

            connection = prepareRequest(POST);
            connection.setRequestProperty("Content-Length",
                    Integer.toString(requestBodyS.getBytes().length));
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(requestBodyS);
            wr.close();

            String responseS = getResponse(connection);

            LogManager.trace(HttpClient.class, "doPost() returned error - " + responseS);
            if(responseS.contains("errors")) {

                ErrorPojo errorPojo =  objectMapper.readValue(responseS, ErrorPojo.class);

                throw new ApplicationException("Server returned error on post request: " + errorPojo);
            }

            T response = objectMapper.readValue(responseS, c);

            return response;


        }catch (ApplicationException exception){
            throw exception;
        } catch (Exception e) {
            LogManager.log(HttpClient.class, e);

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e.getMessage());

        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



    public <T>T doGet (Class<T> c) {

        LogManager.trace(HttpClient.class, "doGet()", url.toString());

        HttpURLConnection connection = null;

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            connection = prepareRequest(GET);
            connection.setUseCaches(false);

            String responseS = getResponse(connection);

            if(responseS.contains("errors")) {

                ErrorPojo errorPojo =  objectMapper.readValue(responseS, ErrorPojo.class);
                LogManager.trace(HttpClient.class, "doPost() returned error - " + errorPojo.getErrors().getErrorsString());

                throw new ApplicationException("Server returned error on get request: " + errorPojo);
            }

            T response = objectMapper.readValue(responseS, c);

            return response;


        }catch (Exception e) {
            LogManager.log(HttpClient.class, e);

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e.getMessage());
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpURLConnection prepareRequest(String methodType) throws IOException {

        HttpURLConnection connection = null;

        if(pathParams != null) {
            addPathParamsToURL();
        }
        if (queryParams != null) {
//            addQueryParamsToURL();
        }
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(methodType);
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setUseCaches(false);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {

                connection.setRequestProperty(entry.getKey(), entry.getValue());

            }
        }

        return connection;

    }

    private void addPathParamsToURL(){
        StringBuilder builder = new StringBuilder(url.toString());

        for (Map.Entry<String, String> entry : pathParams.entrySet()){
            if (builder.lastIndexOf("/") != builder.length()) {
                builder.append("/");
            }
        }
    }

    private String getResponse (HttpURLConnection connection) throws IOException {
        connection.getResponseCode();

        InputStream is = connection.getErrorStream();

        if(is == null) {
            is = connection.getInputStream();
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuffer response = new StringBuffer();
        String line;

        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }

        rd.close();
        return response.toString();

    }

    public static void main (String[] args) {

        try{
            final String charset = "UTF-8";
            String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjAwMDc5NUNFQjZERDlFMUQ2QzFFMzkxNDAyRDY0QTM4QzhERkNEMEUiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJBQWVWenJiZG5oMXNIamtVQXRaS09NamZ6UTQifQ.eyJuYmYiOjE2ODU2MDkxNzgsImV4cCI6MTY4NTYxMjc3OCwiaXNzIjoiaHR0cHM6Ly9hdXRoLXRlc3QuMTBucGF5LmNvbSIsImF1ZCI6WyJJZGVudGl0eVNlcnZlckFwaSIsIlRlbm4uQ1JNIl0sImNsaWVudF9pZCI6IlNpcm1hLk9wZW5CYW5raW5nIiwic3ViIjoiMCIsInRlbmFudCI6IjEyIiwicm9sZSI6Ik9wZW5CYW5raW5nIiwic2NvcGUiOlsiSWRlbnRpdHlTZXJ2ZXJBcGkiLCJUZW5uLkNSTSJdLCJjbmYiOnsieDV0I1MyNTYiOiJFQjNENDk3RUVFMDQ4OTE1MDUyODk4MzAyOUM2Q0IyOTA4Nzg0MUUxIn19.mJJrO6i-EGi3U4jDHJxrdmjB55h2j4LVwTsL1DgwpMcL6aZ8aiMRGWPOGWHcKgshZCAx6mk_GO3o6Uass8ZszPvzgxDtRuJk04IzoNT2nbV66-NZU58BmiAGhfuwU9vCdMRpjUECGPXBvsp8KgbwCr6TucxYq-dD0HROlnMu8mG3iOtjEgLMEKPglQqURviB1KIW6Bvv7IpTmG5L8NUziCxEf7YlSNIgLCb_dv7mJJfNdCBU5pc08IRl3JT0izUqZLaikK2ZN7hh9qD8YGFgvXktlDmEmQ4mJkENsvqhA6tBtT93NjYnC5z4W4gdomcdVzDBx3WeAcXNum-WXM0uDw";
            // Create the connection
            HttpURLConnection connection = (HttpURLConnection) new URL("https://crm-api-test.10npay.com/api/v1/OpenBanking/individual/iban").openConnection();
            // setDoOutput(true) implicitly set's the request type to POST
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
    //        connection.setRequestProperty("Content-Type", contentType);//"text/xml");
            String content = "{\"ibanNumber\": \"BG06TEPJ40131000100029\"}";
            // Write to the connection
            OutputStream output = connection.getOutputStream();
            output.write(content.getBytes(charset));
            output.close();

            // Check the error stream first, if this is null then there have been no issues with the request String error
            int errorCode = connection.getResponseCode();
            System.out.println(errorCode);

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

            System.out.println(response);

        }catch (Exception e) {
            e.printStackTrace();
        }



    }


}
