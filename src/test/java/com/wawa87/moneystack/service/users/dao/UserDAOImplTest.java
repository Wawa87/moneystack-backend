package com.wawa87.moneystack.service.users.dao;

import com.wawa87.moneystack.service.users.db.PGUtil;
import com.wawa87.moneystack.service.users.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

public class UserDAOImplTest {
    @Test
    public void testSave() {
        UserDAOImpl userDAO =new UserDAOImpl(PGUtil.getDataSource());

        User user = new User();
        user.setUserId("user1");

        ArrayList<String> emails = new ArrayList<>();
        emails.add("user1@email1.com");
        emails.add("user1@email2.com");
        emails.add("user1@email3.com");
        user.setEmails(emails);

        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("+16195554321");

        Optional<User> rsUser = userDAO.save(user);
        System.out.println(rsUser.get().getId());
        Assertions.assertTrue((rsUser.isPresent()));
        Assertions.assertTrue(rsUser.get().getId() != null);
    }

    @Test
    public void testFindById() {
        UserDAOImpl userDAO = new UserDAOImpl(PGUtil.getDataSource());

        Optional<User> user = userDAO.findById(Long.valueOf(1));
        Assertions.assertTrue(user.isPresent());
    }
}
