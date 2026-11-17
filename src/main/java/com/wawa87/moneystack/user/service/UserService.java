package com.wawa87.moneystack.user.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.auth.service.AuthenticationService;
import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.common.exceptions.*;
import com.wawa87.moneystack.common.util.ServletUtility;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.*;
import de.mkammerer.argon2.Argon2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserDAO userDAO;
    private Argon2 argon2;
    private AuthorizationService authorizationService;
    private AuthenticationService authenticationService;
    private PasswordResetService passwordResetService;

    public UserService(UserDAO userDAO, Argon2 argon2, AuthenticationService authenticationService, AuthorizationService authorizationService, PasswordResetService passwordResetService) {
        this.userDAO = userDAO;
        this.argon2 = argon2;
        this.authorizationService = authorizationService;
        this.authenticationService = authenticationService;
    }

    public void setPasswordResetService(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    public UserResponse register(UserRequest userRequest) throws InvalidUsernameException, BadRequestException {
        // Transform username to lower case.
        userRequest.setUsername(userRequest.getUsername().toLowerCase());

        // Validate available username.
        this.authenticationService.validateNewUsername(userRequest.getUsername());

        // Process phone number formatting.
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(userRequest.getPhoneNumber(), "US");
            if (phoneNumberUtil.isValidNumber(number)) {
                userRequest.setPhoneNumber(phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164));
            } else {
                throw new BadRequestException("Phone number validation failed.");
            }
        } catch (NumberParseException e) {
            throw new BadRequestException("Phone number validation failed.", e);
        }

        // Hash the password.
        String hash = hashPw(userRequest.getPassword());
        userRequest.setPassword(hash);

        // Save and return User.
        try {
            User user = UserRequest.convertToUser(userRequest);
            Optional<User> res = userDAO.save(user);

            if (res.isPresent()) {
                user = res.get(); // Get the new User with populated id.

                // Create response object.
                UserResponse userResponse = UserResponse.convertUserToResponse(user);
                return userResponse;
            } else {
                throw new BadRequestException("Error registering the new user: " + userRequest.getUsername());
            }
        } catch (Exception e) {
            throw new BadRequestException("Error registering the new user: " + userRequest.getUsername());
        }
    }

    public UserResponse saveNewUser(UserRequest userRequest, String currentUsername) throws ValidationException, InvalidUsernameException, BadRequestException {
        // Authorize admin only.
        if (!this.authorizationService.isAdminRole(currentUsername)) {
            throw new ValidationException();
        }

        // Transform username to lower case.
        userRequest.setUsername(userRequest.getUsername().toLowerCase());

        // Validate available username.
        this.authenticationService.validateNewUsername(userRequest.getUsername());

        // Hash the password.
        String hash = hashPw(userRequest.getPassword());
        userRequest.setPassword(hash);

        // Save new User.
        Optional<User> userOpt = this.userDAO.save(UserRequest.convertToUser(userRequest));
        if (userOpt.isEmpty()) throw new BadRequestException();
        return UserResponse.convertUserToResponse(userOpt.get());
    }

    public void updatePassword(String currentUsername, PasswordUpdateRequest passwordUpdateRequest) throws BadRequestException, ValidationException {
        // Get the User record where password will be updated.
        Optional<User> userOpt = userDAO.findByUsername(currentUsername);
        if (userOpt.isEmpty()) throw new BadRequestException("User not found.");
        User user = userOpt.get();

        // Verify current password.
        if (!this.argon2.verify(user.getPassword(), passwordUpdateRequest.getCurrentPassword())) throw new ValidationException("Current password is incorrect.");

        // Validate new password.
        if (!passwordUpdateRequest.getNewPassword().equals(passwordUpdateRequest.getConfirmNewPassword())) throw new ValidationException("New passwords don't match.");
        // TODO: Implement password requirements

        // Update the password.
        this.setPassword(user, passwordUpdateRequest.getNewPassword());
    }

    public UserResponse findUserById(Long requesterId, Long requestedId) throws NotFoundException, ValidationException {
        if (!authorizationService.authorizeForUser(requesterId, requestedId)) {
            throw new ValidationException();
        }
        Optional<User> userOpt = userDAO.findById(requestedId);
        if (userOpt.isEmpty()) throw new NotFoundException();
        return UserResponse.convertUserToResponse(userOpt.get());
    }

    public UserResponse findUserByUsername(String username) throws NotFoundException {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) throw new NotFoundException();
        return UserResponse.convertUserToResponse(userOpt.get());
    }

    public List<UserResponse> getUsers(String currentUsername) throws ValidationException {
        if (!this.authorizationService.isAdminRole(currentUsername)) {
            throw new ValidationException();
        }

        List<User> users = userDAO.findAll();
        List<UserResponse> usersResponse = new ArrayList<>();
        users.forEach((it) -> {
            usersResponse.add(UserResponse.convertUserToResponse(it));
        });
        return usersResponse;
    }

    public UserResponse updateUser(Long id, User user, String currentUsername) throws ValidationException, NotFoundException, BadRequestException, InvalidUsernameException {
        // Authorize admin only.
        if (!this.authorizationService.isAdminRole(currentUsername)) {
            throw new ValidationException();
        }

        // Transform username to lower case.
        user.setUsername(user.getUsername().toLowerCase());

        // Get current User record from database.
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) throw new NotFoundException(); // Return not found error.

        // Update the User from UserRequest.
        User updatedUser = userOpt.get();
        updatedUser.setUsername(user.getUsername().toLowerCase());
        updatedUser.setEmails((ArrayList<String>) user.getEmails());
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setPhoneNumber(user.getPhoneNumber());

        // Validate available username.
        this.authenticationService.validateNewUsername(updatedUser.getUsername());

        // Update User record.
        int result = userDAO.update(updatedUser); // Returns 1 for row updated. Returns 0 for error/no rows updated.
        if (result == 1) {
            return UserResponse.convertUserToResponse(updatedUser);
        } else {
            throw new BadRequestException();
        }
    }

    public int deleteUser(User user) {
        return userDAO.delete(user);
    }

    public void deleteUserById(Long deleteUserId, Long currentUserId) throws ValidationException, NotFoundException, BadRequestException {
        // Authorize admin only.
        if (!this.authorizationService.isAdminRole(currentUserId)) {
            throw new ValidationException();
        }

        Optional<User> userOpt = userDAO.findById(deleteUserId);
        if (userOpt.isEmpty()) throw new NotFoundException();

        // Delete the User.
        int result = userDAO.deleteById(deleteUserId);
        if (result != 1) throw new BadRequestException();
    }

    /**
     * Hashes the password string according to the Argon2 configuration.
     * @param password
     * @return
     */
    private String hashPw(String password) {
        return this.argon2.hash(22,  65536, 1, password);
    }

    /**
     * The method sets the given password without any validations for the provided User object.
     * This final step when updating a password ensures proper hashing.
     * @param user
     * @param password
     */
    public void setPassword(User user, String password) {
        String hashedPW = this.hashPw(password);
        user.setPassword(hashedPW);
        this.userDAO.update(user);
    }
}
