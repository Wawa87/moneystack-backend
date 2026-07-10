package com.wawa87.moneystack.service.system.user.model;

public class RegistrationResult {
    private UserResponse userResponse;
    private boolean result;
    private String message;

    public RegistrationResult(UserResponse userResponse, boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(UserResponse userResponse) {
        this.userResponse = userResponse;
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

    public static RegistrationResult newRegistrationResult(UserResponse userResponse, boolean result, String message) {
        return new RegistrationResult(userResponse , result, message);
    }
}
