package com.wawa87.moneystack.service.system.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class ValidationException extends ApiException {
    public ValidationException() {
        super("Unauthorized.");
    }
}