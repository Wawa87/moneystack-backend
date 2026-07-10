package com.wawa87.moneystack.service.auth;

public class UsernameValidationResponse {
    private boolean result;
    private String message;

    public UsernameValidationResponse(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static UsernameValidationResponse newValidationResponse(boolean result, String message) {
        return new UsernameValidationResponse(result, message);
    }
}
