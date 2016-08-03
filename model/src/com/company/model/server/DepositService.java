package com.company.model.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.company.model.db.AccountManager;
import com.company.model.db.IAccountManager;
import com.company.model.db.NegativeBalanceException;
import com.company.model.db.WrongAccountIdException;
import com.company.model.db.account.Account;
import com.company.model.db.account.AccountOperation;
import com.company.remote.*;
import org.hibernate.SessionFactory;

public class DepositService extends UnicastRemoteObject implements IDepositServer {
    private static final Logger LOGGER = Logger.getLogger(DepositService.class.getName());
    private static final String SERVICE_NAME = "DepositService";
    private static final int MAX_OPERATIONS_IN_REQUEST = 10;
    private static final int INITIAL_ACCOUNT_FUNDS = 0;

    private final IAccountManager accountManager;

    public DepositService(SessionFactory sessionFactory) throws RemoteException {
        super();
        this.accountManager = new AccountManager(sessionFactory);
    }

    @Override
    public AccountInfo getAccountInfo(Integer accountId) throws RemoteException, NoSuchAccountException {
        if (accountId == null) {
            throw new NullPointerException("Null account id");
        }

        try {
            Account account = accountManager.getAccount(accountId);
            List<AccountOperation> operations = account.getOperations();

            List<OperationInfo> operationInfoList = new ArrayList<>(MAX_OPERATIONS_IN_REQUEST);
            for (int i = operations.size() - 1; i >= 0 && i >= operations.size() - MAX_OPERATIONS_IN_REQUEST; --i) {
                AccountOperation operation = operations.get(i);
                StringBuilder info = new StringBuilder();
                info.append("Account id ")
                        .append(account.getAccountId())
                        .append(", operation ")
                        .append(operation.getType().toString())
                        .append(", amount ")
                        .append(operation.getValue());

                if (operation.getOperandAccountId() != null) {
                    info.append(", to ").append(operation.getOperandAccountId());
                }

                operationInfoList.add(new OperationInfo(info.toString()));
            }

            return new AccountInfo(account.getAccountId(),
                                   account.getBalance(),
                                   operationInfoList);
        } catch (WrongAccountIdException e) {
            throw new NoSuchAccountException("There is no account " + accountId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown exception", e);
            throw new RemoteException("Unknown server exception");
        }
    }

    @Override
    public void createAccount(Integer accountId) throws RemoteException {
        if (accountId == null) {
            throw new NullPointerException("Null account id");
        }

        try {
            accountManager.createNewAccount(accountId, INITIAL_ACCOUNT_FUNDS);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown exception", e);
            throw new RemoteException("Unknown server exception");
        }
    }

    @Override
    public void addFunds(Integer accountId, Integer amount)
            throws RemoteException, NoSuchAccountException, NotEnoughFundsException {
        if (accountId == null || amount == null) {
            throw new NullPointerException("Null parameters");
        }

        try {
            accountManager.addFunds(accountId, amount);
        } catch (WrongAccountIdException e) {
            throw new NoSuchAccountException("There is no account " + accountId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown exception", e);
            throw new RemoteException("Unknown server exception");
        }
    }

    @Override
    public void reduceFunds(Integer accountId, Integer amount)
            throws RemoteException, NoSuchAccountException, NotEnoughFundsException {
        if (accountId == null || amount == null) {
            throw new NullPointerException("Null parameters");
        }

        try {
            accountManager.reduceFunds(accountId, amount);
        } catch (WrongAccountIdException e) {
            throw new NoSuchAccountException("There is no account " + accountId);
        } catch (NegativeBalanceException e) {
            throw new NotEnoughFundsException("Not enough funds on account " + accountId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown exception", e);
            throw new RemoteException("Unknown server exception");
        }
    }

    @Override
    public void transfer(Integer fromId, Integer toId, Integer amount)
            throws RemoteException, NoSuchAccountException, NotEnoughFundsException {
        if (fromId == null || toId == null || amount == null) {
            throw new NullPointerException("Null parameters");
        }

        try {
            accountManager.transfer(fromId, toId, amount);
        } catch (WrongAccountIdException e) {
            throw new NoSuchAccountException("Account not found");
        } catch (NegativeBalanceException e) {
            throw new NotEnoughFundsException("Not enough funds on account " + fromId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown exception", e);
            throw new RemoteException("Unknown server exception");
        }
    }

    public static String getServiceName() {
        return SERVICE_NAME;
    }
}
