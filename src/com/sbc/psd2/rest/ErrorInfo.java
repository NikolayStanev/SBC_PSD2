package com.sbc.psd2.rest;

public class ErrorInfo {
    private int code;
    private String description;

    public ErrorInfo() {
    }

    public ErrorInfo(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
