package com.sbc.psd2.rest.util;

public class MyTestCLass {

    private String id;

    public MyTestCLass() {
    }

    public MyTestCLass(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MyTestCLass{" +
                "id='" + id + '\'' +
                '}';
    }
}
