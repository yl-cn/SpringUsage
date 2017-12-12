package com.spring.exception;

/**
 * 服务异常/失败抛出异常类
 */
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1000010000L;
    //错误编码
    private String errorCode;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String code, String message) {
        super(message);
        this.errorCode = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}

