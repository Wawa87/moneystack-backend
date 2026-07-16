package com.wawa87.moneystack.service.system.exceptions;

public class InvalidUsernameException extends ApiException {
    public InvalidUsernameException() {
        super("Invalid username.");
    }

    public InvalidUsernameException(String message) {
        super(message);
    }
}
