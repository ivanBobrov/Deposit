package com.company.model.db;


public class NegativeBalanceException extends RuntimeException {

    public NegativeBalanceException() {
        super();
    }

    public NegativeBalanceException(String message) {
        super(message);
    }

    public NegativeBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NegativeBalanceException(Throwable cause) {
        super(cause);
    }
}
