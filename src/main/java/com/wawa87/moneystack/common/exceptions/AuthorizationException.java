package com.wawa87.moneystack.common.exceptions;

public class AuthorizationException extends ApiException {
    public AuthorizationException() {
        super("Unauthorized.");
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
