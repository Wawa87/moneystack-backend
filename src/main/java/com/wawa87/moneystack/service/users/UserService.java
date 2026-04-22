package com.wawa87.moneystack.service.users;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.service.users.dao.UserDAO;
import com.wawa87.moneystack.service.users.dao.UserDTO;
import com.wawa87.moneystack.service.users.models.User;
import de.mkammerer.argon2.Argon2;

import java.util.ArrayList;
import java.util.Optional;

public class UserService {
    private UserDAO userDAO;
    private Argon2 argon2;

    public UserService(UserDAO userDAO, Argon2 argon2) {
        this.userDAO = userDAO;
        this.argon2 = argon2;
    }

    public User register(String userId, String email, String firstName, String lastName, String password, String phoneNumber) {
        User user = new User();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.setEmails(new ArrayList<String>() {{
            add(email);
        }});

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, "US");
            if (phoneNumberUtil.isValidNumber(number)) {
                user.setPhoneNumber(phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164));
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        char[] passwordChar = password.toCharArray();

        String hash = hashPw(password);

        user.setPasswordHash(hash);

        Optional<User> res = userDAO.save(user);
        if (res.isPresent()) {
            user = res.get();
            return user;
        }
        return null;
    }

    public boolean authenticate(String userId, String password) {
        Optional<User> res = userDAO.findByUserId(userId);
        if (res.isPresent()) {
            User user = res.get();
            return argon2.verify(user.getPasswordHash(), password);
        }
        return false;
    }

    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        Optional<User> res = userDAO.findByUserId(userId);
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

    public Optional<User> getUser(String userId) {
        Optional<User> res = userDAO.findByUserId(userId);
        return res;
    }

    public Optional<UserDTO> getUserDTO(String userId) {
        Optional<User> res = userDAO.findByUserId(userId);
        UserDTO userDTO = new UserDTO();

        if (res.isPresent()) {
            User user = res.get();
            userDTO.setUserId(user.getUserId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setEmails(user.getEmails());
            userDTO.setPhoneNumber(user.getPhoneNumber());
        }
        return Optional.of(userDTO);
    }

    public int updateUser(User user) {
        return userDAO.update(user);
    }

    public int deleteUser(User user) {
        return userDAO.delete(user);
    }

    private String hashPw(String password) {
        return argon2.hash(22,  65536, 1, password);
    }
}
