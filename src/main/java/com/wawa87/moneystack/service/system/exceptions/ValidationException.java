package com.wawa87.moneystack.service.system.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(HttpServletResponse.SC_BAD_REQUEST, message);
    }
}
