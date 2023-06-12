package com.sbc.psd2.data.tenN.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Errors {

    @JsonProperty("Error")
    public ArrayList<String> error;

    public ArrayList<String> getError() {
        return error;
    }

    public void setError(ArrayList<String> error) {
        this.error = error;
    }

    public String getErrorsString() {
        StringBuffer sb = new StringBuffer();

        for(String current : error){
            sb.append(current);
            sb.append("#");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Errors{" +
                "error=" + error +
                '}';
    }
}
