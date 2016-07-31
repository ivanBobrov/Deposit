package com.company.model.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import com.company.remote.IDepositServer;

public class DepositService extends UnicastRemoteObject implements IDepositServer {
    private static final String SERVICE_NAME = "DepositService";

    public DepositService() throws RemoteException {
        super();
    }

    @Override
    public String getString() throws RemoteException {
        return "Helicopter";
    }

    public static String getServiceName() {
        return SERVICE_NAME;
    }
}
