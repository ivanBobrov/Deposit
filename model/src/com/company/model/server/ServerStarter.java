package com.company.model.server;


import com.company.remote.IDepositServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStarter {
    private static final Logger LOGGER = Logger.getLogger(ServerStarter.class.getName());
    private static final int PORT_NUMBER = 1234;
    private static final String REGISTRY_URL = "rmi://localhost:" + PORT_NUMBER + "/";

    public static void main(String[] args) {
        try {
            startRmiRegistry();
            IDepositServer service = new DepositService();
            Naming.rebind(REGISTRY_URL + DepositService.getServiceName(), service);
        } catch (RemoteException exception) {
            LOGGER.log(Level.SEVERE, "Registry error. Can't start service", exception);
        } catch (MalformedURLException exception) {
            LOGGER.log(Level.SEVERE, "Bad url. Can't start service", exception);
        }
    }

    private static void startRmiRegistry() throws RemoteException {
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(PORT_NUMBER);
            registry.list();
            LOGGER.info("RMI registry found");
        } catch (RemoteException remoteException) {
            // Failed to find registry at that port
            registry = LocateRegistry.createRegistry(PORT_NUMBER);
            LOGGER.info("Created new RMI registry");
        }
    }
}
