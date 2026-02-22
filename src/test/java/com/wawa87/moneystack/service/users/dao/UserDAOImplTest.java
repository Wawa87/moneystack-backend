package com.wawa87.moneystack.service.users.dao;

import com.wawa87.moneystack.service.users.db.PGUtil;
import com.wawa87.moneystack.service.users.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class UserDAOImplTest {
    @Test
    @Disabled
    public void testSave() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO =new UserDAOImpl(connection);

            User user = new User();
            user.setUserId("testuser");

            ArrayList<String> emails = new ArrayList<>();
            emails.add("testuser@email1.com");
            emails.add("testuser@email2.com");
            emails.add("testuser@email3.com");
            user.setEmails(emails);

            user.setFirstName("Test");
            user.setLastName("User");
            user.setPhoneNumber("+16195554321");

            Optional<User> rsUser = userDAO.save(user);
            Assertions.assertTrue((rsUser.isPresent()));
            Assertions.assertTrue(rsUser.get().getId() != null);

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindById() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO =new UserDAOImpl(connection);

            User user = new User();
            user.setUserId("testuser");

            ArrayList<String> emails = new ArrayList<>();
            emails.add("testuser@email1.com");
            emails.add("testuser@email2.com");
            emails.add("testuser@email3.com");
            user.setEmails(emails);

            user.setFirstName("Test");
            user.setLastName("User");
            user.setPhoneNumber("+16195554321");

            Optional<User> rsUser = userDAO.save(user);

            Optional<User> user1 = userDAO.findById(rsUser.get().getId());
            Assertions.assertTrue(user1.isPresent());
            Assertions.assertTrue(user1.get().getUserId().equals("testuser"));
            Assertions.assertTrue(user1.get().getEmails().get(0).equals("testuser@email1.com"));
            Assertions.assertTrue(user1.get().getEmails().get(1).equals("testuser@email2.com"));
            Assertions.assertTrue(user1.get().getEmails().get(2).equals("testuser@email3.com"));
            Assertions.assertTrue(user1.get().getFirstName().equals("Test"));
            Assertions.assertTrue(user1.get().getLastName().equals("User"));
            Assertions.assertTrue(user1.get().getPhoneNumber().equals("+16195554321"));

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
