package com.wawa87.moneystack.service.system.exceptions;

public class NotFoundException extends ApiException {
    public NotFoundException() {
        super("Not found.");
    }
}
