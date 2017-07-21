package com.spring.exception;

public class OrderNotFoundException extends RuntimeException {

    /**
     * Unique ID for Serialized object
     */
    private static final long serialVersionUID = 4657491283614455649L;

    public OrderNotFoundException(String msg) {
        super(msg);
    }

    public OrderNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

}
