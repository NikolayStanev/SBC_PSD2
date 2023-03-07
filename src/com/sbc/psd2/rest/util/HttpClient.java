package com.sbc.psd2.rest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
        LogManager.trace(HttpClient.class, "doPost()", url.toString() , requestBody.toString());

        HttpURLConnection connection = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String requestBodyS;
            if (requestBody instanceof String) {
                requestBodyS = (String) requestBody;
            }else {
                requestBodyS = objectMapper.writeValueAsString(requestBody);
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

    public <T>T doGet (Class<T> c) {

        LogManager.trace(HttpClient.class, "doGet()", url.toString());

        HttpURLConnection connection = null;

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            connection = prepareRequest(GET);
            connection.setUseCaches(false);

            String responseS = getResponse(connection);

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

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
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

        try {
            HttpClient client = new HttpClient(new URL("https://wannatest.free.beeceptor.com/id"),
                    null,
                    null,
                    null ,
                    null,
                    null);

            MyTestCLass myTestCLass = client.doGet(MyTestCLass.class);

            System.out.println(myTestCLass.toString());

        }catch (Exception e) {
            e.printStackTrace();
        }



    }


}
