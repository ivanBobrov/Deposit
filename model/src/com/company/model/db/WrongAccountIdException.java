package com.company.model.db;


public class WrongAccountIdException extends RuntimeException {

    public WrongAccountIdException() {
        super();
    }

    public WrongAccountIdException(String message) {
        super(message);
    }

    public WrongAccountIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongAccountIdException(Throwable cause) {
        super(cause);
    }
}
