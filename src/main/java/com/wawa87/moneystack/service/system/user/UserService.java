package com.wawa87.moneystack.service.system.user;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import com.wawa87.moneystack.service.system.user.model.User;
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

    public User register(String username, String email, String firstName, String lastName, String password, String phoneNumber) {
        User user = new User();
        user.setUsername(username);
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
            userDTO.setUsername(user.getUsername());
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
