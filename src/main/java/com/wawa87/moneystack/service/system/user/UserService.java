package com.wawa87.moneystack.service.system.user;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.service.auth.AuthorizationChecker;
import com.wawa87.moneystack.service.auth.UsernameValidationResponse;
import com.wawa87.moneystack.service.system.exceptions.ValidationException;
import com.wawa87.moneystack.service.system.db.ResultStatus;
import com.wawa87.moneystack.service.system.user.model.RegistrationResult;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.model.UserRequest;
import com.wawa87.moneystack.service.system.user.model.UserResponse;
import com.wawa87.moneystack.service.system.user.model.User;
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

    public UserService(UserDAO userDAO, Argon2 argon2) {
        this.userDAO = userDAO;
        this.argon2 = argon2;
    }

    public RegistrationResult register(UserRequest userRequest) {
        // Transform username to lower case.
        userRequest.setUsername(userRequest.getUsername().toLowerCase());

        // Validate available username.
        UsernameValidationResponse usernameValidationResponse = validateNewUsername(userRequest.getUsername());
        if (!usernameValidationResponse.getResult()) {
            return RegistrationResult.newRegistrationResult(null , false, usernameValidationResponse.getMessage());
        }

        // Process phone number formatting.
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(userRequest.getPhoneNumber(), "US");
            if (phoneNumberUtil.isValidNumber(number)) {
                userRequest.setPhoneNumber(phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164));
            } else {
                return RegistrationResult.newRegistrationResult(null, false, "Phone number validation failed.");
            }
        } catch (NumberParseException e) {
            logger.error(e.getLocalizedMessage());
            return RegistrationResult.newRegistrationResult(null, false, "Phone number validation failed.");
        }

        // Hash the password.
        String hash = hashPw(userRequest.getPassword());
        userRequest.setPassword(hash);

        // Save and return User.
        try {
            User user = UserRequest.convertToUser(userRequest);
            Optional<User> res = userDAO.save(user);

            if (res.isPresent()) {
                user = res.get();

                // Create response object.
                UserResponse userResponse = UserResponse.convertUserToResponse(user);
                return new RegistrationResult(userResponse, true, "Registered new user: " + userResponse.getUsername());
            } else {
                return new RegistrationResult(null, false, "Error registering new user: " + userRequest.getUsername());
            }
        } catch (Exception e) {
            return new RegistrationResult(null, false, "Error registering new user: " + userRequest.getUsername());
        }
    }

    public UserResponse authenticate(String username, String password) {
        Optional<User> res = userDAO.findByUsername(username);
        if (res.isPresent()) {
            User user = res.get();
            boolean validPw = argon2.verify(user.getPasswordHash(), password);

            if (validPw) {
                return UserResponse.convertUserToResponse(user);
            }
        }
        return null;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> res = userDAO.findByUsername(username);
        if (res.isPresent()) {
            if (argon2.verify(res.get().getPasswordHash(), oldPassword)) {
                String updatePw = hashPw(newPassword);
                User user = res.get();
                user.setPasswordHash(updatePw);
                userDAO.update(user);
                return true;
            }
        }
        return false;
    }

    public UserResponse findUserById(Long id) {
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) return null;
        return UserResponse.convertUserToResponse(userOpt.get());
    }

    public UserResponse findUserByUsername(String username) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) return null;
        return UserResponse.convertUserToResponse(userOpt.get());
    }

    public UsernameValidationResponse validateNewUsername(String username) {
        if (username == null || username.isBlank()) return UsernameValidationResponse.newValidationResponse(false, "Username is blank."); // Check for null, empty, or whitespace.
        if (!username.matches("^[a-zA-Z0-9]+$")) return UsernameValidationResponse.newValidationResponse(false, "Username must be alphanumeric characters only."); // Check for alphanumeric characters only.

        Optional<User> userOpt = userDAO.findByUsername(username.toLowerCase());
        if (userOpt.isPresent()) return UsernameValidationResponse.newValidationResponse(false, "Username is already taken."); // Check if username is already taken.
        return UsernameValidationResponse.newValidationResponse(true, "Username is available: " + username.toLowerCase());
    }

    public List<UserResponse> getUsers(String username) {
        // TODO: Implement proper authorization check.
        if (!AuthorizationChecker.authorizeAdminUsername(username)) {
            throw new ValidationException("Unauthorized. Admin access only.");
        }

        List<User> users = userDAO.findAll();
        List<UserResponse> usersResponse = new ArrayList<>();
        users.forEach((it) -> {
            usersResponse.add(UserResponse.convertUserToResponse(it));
        });
        return usersResponse;
    }

    public ResultStatus updateUser(Long id, UserRequest userRequest) {
        // Transform username to lower case.
        userRequest.setUsername(userRequest.getUsername().toLowerCase());

        // Return matching User record from database.
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) return ResultStatus.NOT_FOUND; // Return not found error.

        // Update the User.
        User updatedUser = userOpt.get();
        updatedUser.setUsername(userRequest.getUsername());
        updatedUser.setEmails((ArrayList<String>) userRequest.getEmails());
        updatedUser.setFirstName(userRequest.getFirstName());
        updatedUser.setLastName(userRequest.getLastName());
        updatedUser.setPhoneNumber(userRequest.getPhoneNumber());
        updatedUser.setPasswordHash(userRequest.getPassword());

        // Return the result code. Success == 1, Error == 0.
        int result = userDAO.update(updatedUser); // Returns 1 for row updated. Returns 0 for error/no rows updated.
        if (result == 1) return ResultStatus.SUCCESS;
        else return ResultStatus.ERROR;
    }

    public int deleteUser(User user) {
        return userDAO.delete(user);
    }

    public ResultStatus deleteUserById(Long id, Long byUserId) {
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) return ResultStatus.NOT_FOUND; // Return not found error.
        // TODO: Implement authorization check.

        int result = userDAO.deleteById(id);
        if (result == 1) return ResultStatus.SUCCESS;
        else return ResultStatus.ERROR;
    }

    private String hashPw(String password) {
        return argon2.hash(22,  65536, 1, password);
    }
}
