package com.wawa87.moneystack.common.exceptions;

public class NotFoundException extends ApiException {
    public NotFoundException() {
        super("Not found.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
