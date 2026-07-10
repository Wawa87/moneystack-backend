package com.wawa87.moneystack.service.system.exceptions;

public class ApiException extends RuntimeException {
    private final int statusCode;

    protected  ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    protected ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    protected ApiException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
