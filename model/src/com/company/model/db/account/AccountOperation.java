package com.company.model.db.account;


import javax.persistence.*;
import java.io.Serializable;

@Entity
public class AccountOperation implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer operationId;

    @Version
    private Integer version;

    @ManyToOne
    private Account account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationType type;

    @Column(nullable = true)
    private Integer value;

    @Column(nullable = true)
    private Integer operandAccountId;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getOperandAccountId() {
        return operandAccountId;
    }

    public void setOperandAccountId(Integer operandAccountId) {
        this.operandAccountId = operandAccountId;
    }
}
