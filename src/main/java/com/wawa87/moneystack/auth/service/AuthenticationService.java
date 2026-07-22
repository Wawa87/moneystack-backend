package com.wawa87.moneystack.auth.service;

import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.user.model.UserResponse;

public interface AuthenticationService {
    UserResponse login(String username, String password) throws ValidationException;
    boolean validateNewUsername(String username) throws InvalidUsernameException;
}
