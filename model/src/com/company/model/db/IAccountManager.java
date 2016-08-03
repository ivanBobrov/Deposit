package com.company.model.db;


import com.company.model.db.account.Account;

public interface IAccountManager {

    Account getAccount(Integer accountId);

    void createNewAccount(Integer accountId, Integer funds);

    void addFunds(Integer accountId, Integer fundsAmount);

    void reduceFunds(Integer accountId, Integer fundsAmount);

    void setFunds(Integer accountId, Integer fundsAmount);

    void transfer(Integer sourceId, Integer destinationId, Integer fundsAmount);

}
