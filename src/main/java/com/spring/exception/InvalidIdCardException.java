package com.spring.exception;

public class InvalidIdCardException extends RuntimeException {

    /**
     * Unique ID for Serialized object
     */
    private static final long serialVersionUID = 4657491283614455649L;

    public InvalidIdCardException(String msg) {
        super(msg);
    }

    public InvalidIdCardException(String msg, Throwable t) {
        super(msg, t);
    }

}
