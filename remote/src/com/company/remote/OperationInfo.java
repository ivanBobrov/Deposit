package com.company.remote;


import java.io.Serializable;

public class OperationInfo implements Serializable {
    private final String info;

    public OperationInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }
}
