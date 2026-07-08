package com.wawa87.moneystack.service.system.user;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.service.system.budget.BudgetService;
import com.wawa87.moneystack.service.system.db.ResultStatus;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import com.wawa87.moneystack.service.system.user.dao.UserRequest;
import com.wawa87.moneystack.service.system.user.dao.UserResponse;
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

    public User register(User user) {
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

    public User saveUser(User user) {
        return register(user);
    }

    public UserResponse saveNewUser(UserRequest userRequest) {
        return UserResponse.convertUserToResponse(register(UserRequest.convertToUser(userRequest)));
    }

    public boolean authenticate(String username, String password) {
        Optional<User> res = userDAO.findByUsername(username);
        if (res.isPresent()) {
            User user = res.get();
            return argon2.verify(user.getPasswordHash(), password);
        }
        return false;
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

    public Optional<User> getUser(String username) {
        Optional<User> res = userDAO.findByUsername(username);
        return res;
    }

    public Optional<UserDTO> getUserDTO(String username) {
        Optional<User> res = userDAO.findByUsername(username);
        UserDTO userDTO = new UserDTO();

        if (res.isPresent()) {
            User user = res.get();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setEmails(user.getEmails());
            userDTO.setPhoneNumber(user.getPhoneNumber());
        }
        return Optional.of(userDTO);
    }

    public UserResponse findUserById(Long id) {
        Optional<User> userOpt = userDAO.findById(id);
        if (userOpt.isEmpty()) return null;
        return UserResponse.convertUserToResponse(userOpt.get());
    }

    public User findUserByUsername(String username) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) return null;
        else return userOpt.get();
    }

//    public int updateUser(User user) {
//        return userDAO.update(user);
//    }

    public ResultStatus updateUser(User user) {
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

    public List<User> getUsers() {
        // TODO: Implement authorization check.
        return userDAO.findAll();
    }

    private String hashPw(String password) {
        return argon2.hash(22,  65536, 1, password);
    }
}
