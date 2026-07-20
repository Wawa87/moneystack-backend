package com.wawa87.moneystack.common.exceptions;

public class NotFoundException extends ApiException {
    public NotFoundException() {
        super("Not found.");
    }
}
