package com.company.model.db.account;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Account implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Version
    private Integer version;

    @Column(nullable = false)
    private Integer accountId;

    @Column(nullable = false)
    private Integer balance;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountOperation> operations;

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        if (accountId != null) {
            this.accountId = accountId;
        }
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer newBalance) {
        if (newBalance != null) {
            if (newBalance < 0) {
                throw new IllegalArgumentException("balance must be positive value");
            }

            balance = newBalance;
        }
    }

    public List<AccountOperation> getOperations() {
        return this.operations;
    }

    public void addOperation(AccountOperation operation) {
        this.operations.add(operation);
    }

    public void setOperations(List<AccountOperation> operations) {
        this.operations = operations;
    }
}
