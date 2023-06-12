package com.sbc.psd2.data.tenN.pojo;

public class ErrorPojo {

    public Errors errors;
    public String type;
    public String title;

    public int status;
    public String traceId;


    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "ErrorPojo{" +
                "errors=" + errors +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
