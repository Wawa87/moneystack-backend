package com.wawa87.moneystack.auth.service;

import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.User;
import com.wawa87.moneystack.user.model.UserResponse;
import de.mkammerer.argon2.Argon2;

import java.util.Optional;

public class AuthenticationServiceImpl implements AuthenticationService {
    private Argon2 argon2;
    private UserDAO userDAO;

    public AuthenticationServiceImpl() {}

    public AuthenticationServiceImpl(Argon2 argon2, UserDAO userDAO) {
        this.argon2 = argon2;
        this.userDAO = userDAO;
    }

    public Argon2 getArgon2() {
        return argon2;
    }

    public void setArgon2(Argon2 argon2) {
        this.argon2 = argon2;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserResponse login(String username, String password) throws ValidationException {
        Optional<User> res = this.userDAO.findByUsername(username);
        User user = res.get();
        boolean validPw = argon2.verify(user.getPassword(), password);

        if (validPw) {
            return UserResponse.convertUserToResponse(user);
        } else {
            throw new ValidationException("Invalid username or password.");
        }
    }

    @Override
    public boolean validateNewUsername(String username) throws InvalidUsernameException {
        if (username == null || username.isBlank()) throw new InvalidUsernameException("Username is blank."); // Check for null, empty, or whitespace.
        if (!username.matches("^[a-zA-Z0-9]+$")) throw new InvalidUsernameException("Username must be alphanumeric characters only."); // Check for alphanumeric characters only.

        Optional<User> userOpt = this.userDAO.findByUsername(username.toLowerCase());
        if (userOpt.isPresent()) throw new InvalidUsernameException("Username is already taken."); // Check if username is already taken.
        return true;
    }
}
