package com.wawa87.moneystack.common.exceptions;

public class ValidationException extends ApiException {
    public ValidationException() {
        super("Invalid.");
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}