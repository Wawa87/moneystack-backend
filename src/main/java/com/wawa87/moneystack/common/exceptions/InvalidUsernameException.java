package com.wawa87.moneystack.common.exceptions;

public class InvalidUsernameException extends ApiException {
    public InvalidUsernameException() {
        super("Invalid username.");
    }

    public InvalidUsernameException(String message) {
        super(message);
    }

    public InvalidUsernameException(Throwable cause) {
        super(cause);
    }

    public InvalidUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
