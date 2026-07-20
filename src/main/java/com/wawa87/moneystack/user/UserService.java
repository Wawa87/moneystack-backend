package com.wawa87.moneystack.user;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.InvalidUsernameException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import com.wawa87.moneystack.user.model.User;
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

    public UserService(UserDAO userDAO, Argon2 argon2, AuthorizationService authorizationService) {
        this.userDAO = userDAO;
        this.argon2 = argon2;
        this.authorizationService = authorizationService;
    }

    public UserResponse register(UserRequest userRequest) throws InvalidUsernameException, BadRequestException {
        // Transform username to lower case.
        userRequest.setUsername(userRequest.getUsername().toLowerCase());

        // Validate available username.
        validateNewUsername(userRequest.getUsername());

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
        validateNewUsername(userRequest.getUsername());

        // Hash the password.
        String hash = hashPw(userRequest.getPassword());
        userRequest.setPassword(hash);

        // Save new User.
        Optional<User> userOpt = this.userDAO.save(UserRequest.convertToUser(userRequest));
        if (userOpt.isEmpty()) throw new BadRequestException();
        return UserResponse.convertUserToResponse(userOpt.get());
    }

    public UserResponse authenticate(String username, String password) throws ValidationException {
        Optional<User> res = userDAO.findByUsername(username);
        User user = res.get();
        boolean validPw = argon2.verify(user.getPassword(), password);

        if (validPw) {
            return UserResponse.convertUserToResponse(user);
        } else {
            throw new ValidationException("Invalid username or password.");
        }
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> res = userDAO.findByUsername(username);
        if (res.isPresent()) {
            if (argon2.verify(res.get().getPassword(), oldPassword)) {
                String updatePw = hashPw(newPassword);
                User user = res.get();
                user.setPassword(updatePw);
                userDAO.update(user);
                return true;
            }
        }
        return false;
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
        validateNewUsername(updatedUser.getUsername());

        // Hash the password.
        String hash = hashPw(updatedUser.getPassword());
        updatedUser.setPassword(hash);

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

    private String hashPw(String password) {
        return argon2.hash(22,  65536, 1, password);
    }

    // Validation methods.
    public boolean validateNewUsername(String username) throws InvalidUsernameException {
        if (username == null || username.isBlank()) throw new InvalidUsernameException("Username is blank."); // Check for null, empty, or whitespace.
        if (!username.matches("^[a-zA-Z0-9]+$")) throw new InvalidUsernameException("Username must be alphanumeric characters only."); // Check for alphanumeric characters only.

        Optional<User> userOpt = userDAO.findByUsername(username.toLowerCase());
        if (userOpt.isPresent()) throw new InvalidUsernameException("Username is already taken."); // Check if username is already taken.
        return true;
    }
}
