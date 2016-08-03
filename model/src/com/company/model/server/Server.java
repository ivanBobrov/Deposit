package com.company.model.server;


import com.company.remote.IDepositServer;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT_NUMBER = 1234;
    private static final String REGISTRY_URL = "rmi://localhost:" + PORT_NUMBER + "/";
    private static final String HIBERNATE_XML = "hibernate.cfg.xml";

    public static void main(String[] args) {
        try {
            startRmiRegistry();
            Configuration configuration = new Configuration();
            configuration.configure(HIBERNATE_XML);
            SessionFactory factory = configuration.buildSessionFactory();
            IDepositServer service = new DepositService(factory);
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
