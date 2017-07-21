package com.spring.exception;

public class UnhandledException extends RuntimeException {

    /**
     * Unique ID for Serialized object
     */
    private static final long serialVersionUID = 4657491283614455649L;

    public UnhandledException(String msg) {
        super(msg);
    }

    public UnhandledException(String msg, Throwable t) {
        super(msg, t);
    }

}
