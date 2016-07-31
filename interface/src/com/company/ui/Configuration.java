package com.company.ui;


public class Configuration {
    private static final Configuration INSTANCE =  new Configuration();

    private final String serviceIp = "localhost";
    private final String serviceName = "DepositService";
    private final int servicePortNumber = 1234;

    private Configuration() {

    }

    public static Configuration getConfiguration() {
        return INSTANCE;
    }

    public String getServiceUrl() {
        return "rmi://" + serviceIp + ":" + servicePortNumber + "/" + serviceName;
    }
}
