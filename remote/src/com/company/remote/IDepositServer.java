package com.company.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Deposit service interface
 */
public interface IDepositServer extends Remote {

    String getString() throws RemoteException;

}
