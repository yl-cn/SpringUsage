package com.spring.exception;

public class InvalidCreditCardException extends RuntimeException {

    /**
     * Unique ID for Serialized object
     */
    private static final long serialVersionUID = 4657491283614455649L;

    public InvalidCreditCardException(String msg) {
        super(msg);
    }

    public InvalidCreditCardException(String msg, Throwable t) {
        super(msg, t);
    }

}
