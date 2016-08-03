package com.company.remote;


import java.io.Serializable;
import java.util.List;

public class AccountInfo implements Serializable {
    private final Integer accountId;
    private final Integer balance;
    private final List<OperationInfo> operationInfoList;

    public AccountInfo(Integer accountId, Integer balance, List<OperationInfo> operationInfoList) {
        this.accountId = accountId;
        this.balance = balance;
        this.operationInfoList = operationInfoList;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public Integer getBalance() {
        return balance;
    }

    public List<OperationInfo> getOperationInfoList() {
        return operationInfoList;
    }
}
