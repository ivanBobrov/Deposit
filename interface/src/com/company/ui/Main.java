package com.company.ui;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import com.company.remote.IDepositServer;

public class Main {

    public static void main(String[] args) {
        try {
            Configuration config = Configuration.getConfiguration();
            IDepositServer depositService = (IDepositServer) Naming.lookup(config.getServiceUrl());
            System.out.println(depositService.getString());
        } catch (RemoteException exception) {
            System.out.println("Can't find server");
        } catch (MalformedURLException exception) {
            System.out.println("bad url");
        } catch (NotBoundException exception) {
            System.out.println("Service not found in registry");
        }

    }
}
