package com.wawa87.moneystack.service.system.user;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.service.auth.UsernameValidationResponse;
import com.wawa87.moneystack.service.system.db.ResultStatus;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserRequest;
import com.wawa87.moneystack.service.system.user.dao.UserResponse;
import com.wawa87.moneystack.service.system.user.model.User;
import de.mkammerer.argon2.Argon2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public User register(User user) {
        // Transform username to lower case.
        user.setUsername(user.getUsername().toLowerCase());

        // Process phone number formatting.
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(user.getPhoneNumber(), "US");
            if (phoneNumberUtil.isValidNumber(number)) {
                user.setPhoneNumber(phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164));
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        // Hash the password.
        String hash = hashPw(user.getPasswordHash());
        user.setPasswordHash(hash);

        // Save and return User.
        Optional<User> res = userDAO.save(user);
        if (res.isPresent()) {
            user = res.get();
            return user;
        }
        return null;
    }

    public UserResponse saveNewUser(UserRequest userRequest) {
        return UserResponse.convertUserToResponse(register(UserRequest.convertToUser(userRequest)));
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

    public List<User> getUsers() {
        // TODO: Implement authorization check.
        return userDAO.findAll();
    }

    public ResultStatus updateUser(User user) {
        // Transform username to lower case.
        user.setUsername(user.getUsername().toLowerCase());

        // Return matching User record from database.
        Optional<User> userOpt = userDAO.findById(user.getId());
        if (userOpt.isEmpty()) return ResultStatus.NOT_FOUND; // Return not found error.
        // TODO: Implement authorization check.

        // Update the User.
        User updatedUser = userOpt.get();
        updatedUser.setUsername(user.getUsername());
        updatedUser.setEmails(user.getEmails());
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setPhoneNumber(user.getPhoneNumber());

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
