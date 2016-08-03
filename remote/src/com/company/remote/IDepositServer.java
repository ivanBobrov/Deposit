package com.company.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Deposit service interface
 */
public interface IDepositServer extends Remote {

    AccountInfo getAccountInfo(Integer accountId) throws RemoteException, NoSuchAccountException;

    void createAccount(Integer accountId) throws RemoteException;

    void addFunds(Integer accountId, Integer amount)
            throws RemoteException, NoSuchAccountException, NotEnoughFundsException;

    void reduceFunds(Integer accountId, Integer amount)
            throws RemoteException, NoSuchAccountException, NotEnoughFundsException;

    void transfer(Integer fromId, Integer toId, Integer amount)
            throws RemoteException, NoSuchAccountException, NotEnoughFundsException;

}
