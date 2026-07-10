package com.wawa87.moneystack.service.system.exceptions;

import jakarta.servlet.http.HttpServletResponse;

public class InternalServerException extends ApiException {
    public InternalServerException(String message, Throwable cause) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message, cause);
    }
}
