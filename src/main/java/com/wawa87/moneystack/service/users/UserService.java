package com.wawa87.moneystack.service.users;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wawa87.moneystack.service.users.dao.UserDAO;
import com.wawa87.moneystack.service.users.dao.UserDAOImpl;
import com.wawa87.moneystack.service.users.db.PGUtil;
import com.wawa87.moneystack.service.users.models.User;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.sql.Connection;
import java.sql.SQLException;
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

        String hash = argon2.hash(22, 65536, 1, password);

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

    // TODO: Implement changePassword method.
    public void changePassword(String userId, String oldPassword, String newPassword) {

    }
}
