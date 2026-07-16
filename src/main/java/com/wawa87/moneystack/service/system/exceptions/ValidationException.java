package com.wawa87.moneystack.service.system.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class ValidationException extends ApiException {
    public ValidationException() {
        super("Unauthorized.");
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