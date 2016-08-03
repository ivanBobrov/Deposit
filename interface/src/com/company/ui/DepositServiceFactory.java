package com.company.ui;


import com.company.remote.IDepositServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DepositServiceFactory {
    private static final Logger LOGGER = Logger.getLogger(DepositServiceFactory.class.getName());
    private static final String serviceIp = "localhost";
    private static final String serviceName = "DepositService";
    private static final int servicePortNumber = 1234;
    private static final IDepositServer depositServer = obtainService();

    public static IDepositServer getDepositService() {
        if (depositServer == null) {
            throw new IllegalStateException("Connection error");
        }

        return depositServer;
    }

    private static IDepositServer obtainService() {
        try {
            return (IDepositServer) Naming.lookup(getServiceUrl());
        } catch (NotBoundException exception) {
            LOGGER.log(Level.SEVERE, "Can't find remote service", exception);
            return null;
        } catch (MalformedURLException exception) {
            LOGGER.log(Level.SEVERE, "Malformed service url", exception);
            return null;
        } catch (RemoteException exception) {
            LOGGER.log(Level.SEVERE, "Remote service bad connection", exception);
            return null;
        }
    }

    private static String getServiceUrl() {
        return "rmi://" + serviceIp + ":" + servicePortNumber + "/" + serviceName;
    }
}
