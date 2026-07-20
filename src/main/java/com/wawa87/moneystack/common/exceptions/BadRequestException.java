package com.wawa87.moneystack.common.exceptions;

public class BadRequestException extends ApiException {
    public BadRequestException() {
        super("Bad request.");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
