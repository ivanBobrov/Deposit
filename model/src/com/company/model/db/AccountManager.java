package com.company.model.db;


import com.company.model.db.account.Account;
import com.company.model.db.account.AccountOperation;
import com.company.model.db.account.OperationType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.OptimisticLockException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AccountManager implements IAccountManager {
    private static final String ACCOUNT_ID_COLUMN = "accountId";
    private static final int MAX_RETRIES_COUNT = 5;

    private final SessionFactory sessionFactory;

    public AccountManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Account getAccount(Integer accountId) {
        Account account;
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            CriteriaQuery<Account> criteria = getCriteriaAccountByAccountId(session, accountId);
            List<Account> accountList = session.createQuery(criteria).list();

            if (accountList.isEmpty()) {
                throw new WrongAccountIdException("There is no account: " + accountId);
            }

            account = accountList.get(0);
            List<AccountOperation> accountOperations = account.getOperations();
            accountOperations.size(); // To initialize lazy objects

            session.save(account);

            session.getTransaction().commit();
        } catch (RuntimeException exception) {
            session.getTransaction().rollback();
            throw exception;
        }

        return account;
    }

    @Override
    public void createNewAccount(Integer accountId, Integer funds) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            CriteriaQuery<Account> criteria = getCriteriaAccountByAccountId(session, accountId);
            List<Account> accountList = session.createQuery(criteria).list();

            if (!accountList.isEmpty()) {
                throw new IllegalArgumentException("Account " + accountId + " already exists");
            }

            Account account = new Account();
            account.setAccountId(accountId);
            account.setBalance(funds);
            account.setOperations(new ArrayList<>());

            AccountOperation createOperation = new AccountOperation();
            createOperation.setType(OperationType.CREATE);
            createOperation.setAccount(account);
            createOperation.setValue(funds);
            account.addOperation(createOperation);

            session.save(account);

            session.getTransaction().commit();
        } catch (RuntimeException exception) {
            session.getTransaction().rollback();
            throw exception;
        }
    }

    @Override
    public void addFunds(Integer accountId, Integer fundsAmount) {
        int retries = 0;
        while(true) {
            try {
                Session session = sessionFactory.getCurrentSession();
                try {
                    session.beginTransaction();
                    CriteriaQuery<Account> criteria = getCriteriaAccountByAccountId(session, accountId);
                    List<Account> accountList = session.createQuery(criteria).list();

                    if (accountList.isEmpty()) {
                        throw new WrongAccountIdException("There is no account: " + accountId);
                    }

                    Account account = accountList.get(0);
                    Integer balance = account.getBalance();
                    account.setBalance(balance + fundsAmount);

                    AccountOperation operation = new AccountOperation();
                    operation.setType(OperationType.ADDITION);
                    operation.setValue(fundsAmount);
                    operation.setAccount(account);
                    account.addOperation(operation);

                    session.getTransaction().commit();
                } catch (RuntimeException exception) {
                    session.getTransaction().rollback();
                    throw exception;
                }

                return;
            } catch (OptimisticLockException e) {
                if (retries++ >= MAX_RETRIES_COUNT) {
                    throw e;
                }
            }
        }
    }

    @Override
    public void reduceFunds(Integer accountId, Integer fundsAmount) {
        int retries = 0;
        while(true) {
            try {
                Session session = sessionFactory.getCurrentSession();
                try {
                    session.beginTransaction();
                    CriteriaQuery<Account> criteria = getCriteriaAccountByAccountId(session, accountId);
                    List<Account> accountList = session.createQuery(criteria).list();

                    if (accountList.isEmpty()) {
                        throw new WrongAccountIdException("There is no account: " + accountId);
                    }

                    Account account = accountList.get(0);
                    Integer balance = account.getBalance();
                    if (balance - fundsAmount < 0) {
                        throw new NegativeBalanceException("Not enough funds. Balance: "
                                                                   + balance + ", need: " + fundsAmount);
                    }

                    account.setBalance(balance - fundsAmount);

                    AccountOperation operation = new AccountOperation();
                    operation.setType(OperationType.REDUCTION);
                    operation.setValue(fundsAmount);
                    operation.setAccount(account);
                    account.addOperation(operation);

                    session.getTransaction().commit();
                } catch (RuntimeException exception) {
                    session.getTransaction().rollback();
                    throw exception;
                }

                return;
            } catch (OptimisticLockException e) {
                if (retries++ >= MAX_RETRIES_COUNT) {
                    throw e;
                }
            }
        }
    }

    @Override
    public void setFunds(Integer accountId, Integer fundsAmount) {
        if (fundsAmount < 0) {
            throw new IllegalArgumentException("Can't set negative balance");
        }

        int retries = 0;
        while (true) {
            try {
                Session session = sessionFactory.getCurrentSession();
                try {
                    session.beginTransaction();
                    CriteriaQuery<Account> criteria = getCriteriaAccountByAccountId(session, accountId);
                    List<Account> accountList = session.createQuery(criteria).list();

                    if (accountList.isEmpty()) {
                        throw new WrongAccountIdException("There is no account: " + accountId);
                    }

                    Account account = accountList.get(0);
                    account.setBalance(fundsAmount);

                    AccountOperation operation = new AccountOperation();
                    operation.setType(OperationType.SET);
                    operation.setValue(fundsAmount);
                    operation.setAccount(account);
                    account.addOperation(operation);

                    session.getTransaction().commit();
                } catch (RuntimeException exception) {
                    session.getTransaction().rollback();
                    throw exception;
                }

                return;
            } catch (OptimisticLockException e) {
                if (retries++ >= MAX_RETRIES_COUNT) {
                    throw e;
                }
            }
        }
    }

    @Override
    public void transfer(Integer sourceId, Integer destinationId, Integer fundsAmount) {
        if (fundsAmount < 0) {
            throw new IllegalArgumentException("Can't transfer negative funds amount");
        }

        if (sourceId.equals(destinationId)) {
            return;
        }

        int retries = 0;
        while (true) {
            try {
                Session session = sessionFactory.getCurrentSession();
                try {
                    session.beginTransaction();
                    CriteriaQuery<Account> criteriaSource = getCriteriaAccountByAccountId(session, sourceId);
                    CriteriaQuery<Account> criteriaDest = getCriteriaAccountByAccountId(session, destinationId);
                    List<Account> sourceList = session.createQuery(criteriaSource).list();
                    List<Account> destinationList = session.createQuery(criteriaDest).list();

                    if (sourceList.isEmpty() || destinationList.isEmpty()) {
                        throw new WrongAccountIdException("Transfer error. Account not exists");
                    }

                    Account source = sourceList.get(0);
                    Account destination = destinationList.get(0);

                    Integer sourceBalance = source.getBalance();
                    if (sourceBalance - fundsAmount < 0) {
                        throw new NegativeBalanceException("Not enough funds. Balance: " + sourceBalance
                                                                   + ", need: " + fundsAmount);

                    }

                    source.setBalance(sourceBalance - fundsAmount);
                    destination.setBalance(destination.getBalance() + fundsAmount);

                    AccountOperation srcOperation = new AccountOperation();
                    srcOperation.setType(OperationType.TRANSFER_SOURCE);
                    srcOperation.setAccount(source);
                    srcOperation.setValue(fundsAmount);
                    srcOperation.setOperandAccountId(destinationId);
                    source.addOperation(srcOperation);

                    AccountOperation destOperation = new AccountOperation();
                    destOperation.setType(OperationType.TRANSFER_DEST);
                    destOperation.setAccount(destination);
                    destOperation.setValue(fundsAmount);
                    destOperation.setOperandAccountId(sourceId);
                    destination.addOperation(destOperation);

                    session.getTransaction().commit();
                } catch (RuntimeException exception) {
                    session.getTransaction().rollback();
                    throw exception;
                }

                return;
            } catch (OptimisticLockException e) {
                if (retries++ >= MAX_RETRIES_COUNT) {
                    throw e;
                }
            }
        }
    }

    private CriteriaQuery<Account> getCriteriaAccountByAccountId(Session session, Integer id) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> root = criteria.from(Account.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get(ACCOUNT_ID_COLUMN), id));

        return criteria;
    }
}
