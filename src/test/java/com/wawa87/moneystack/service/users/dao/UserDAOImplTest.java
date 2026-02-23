package com.wawa87.moneystack.service.users.dao;

import com.wawa87.moneystack.service.users.db.PGUtil;
import com.wawa87.moneystack.service.users.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImplTest {
    @Test
    public void testSave() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO =new UserDAOImpl(connection);

            // Create the test User.
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

            // Insert the test User and confirm id generation of new record.
            Optional<User> rsUser = userDAO.save(user);
            Assertions.assertTrue((rsUser.isPresent()));
            Assertions.assertNotNull(rsUser.get().getId());

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

            // Create the test user.
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

            // Save the test user and update the reference object with field values from insert (id, createAt).
            user = (userDAO.save(user)).get();

            // Test the findById() method.
            Optional<User> user1 = userDAO.findById(user.getId());

            Assertions.assertTrue(user1.isPresent());
            Assertions.assertEquals("testuser", user1.get().getUserId());
            Assertions.assertEquals("testuser@email1.com", user1.get().getEmails().get(0));
            Assertions.assertEquals("testuser@email2.com", user1.get().getEmails().get(1));
            Assertions.assertEquals("testuser@email3.com", user1.get().getEmails().get(2));
            Assertions.assertEquals("Test", user1.get().getFirstName());
            Assertions.assertEquals("User", user1.get().getLastName());
            Assertions.assertEquals("+16195554321", user1.get().getPhoneNumber());

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAll() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);

            // Create test Users.
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

            User user1 = new User();
            user1.setUserId("testuser1");

            ArrayList<String> emails1 = new ArrayList<>();
            emails1.add("testuser1@email1.com");
            emails1.add("testuser1@email2.com");
            emails1.add("testuser1@email3.com");
            user.setEmails(emails1);

            user.setFirstName("Test1");
            user.setLastName("User1");
            user.setPhoneNumber("+16195554321");

            user = (userDAO.save(user)).get();
            user1 = (userDAO.save(user1)).get();

            // Test the findAll() method.
            List<User> users = userDAO.findAll();

            Assertions.assertEquals(2, users.size());

            User resUser = users.getFirst();
            User resUser1 = users.getLast();

            Assertions.assertEquals(user.getUserId(), resUser.getUserId());
            Assertions.assertEquals(user1.getUserId(), resUser1.getUserId());

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);

            // Create test Users.
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

            user = (userDAO.save(user)).get();

            // Update values on the User object.
            user.setUserId("ckramer");

            emails.clear();
            emails.add("ckramer@email1.com");
            emails.add("ckramer@email2.com");
            emails.add("ckramer@email3.com");

            user.setFirstName("Cosmo");
            user.setLastName("Kramer");
            user.setPhoneNumber("+18385554321");

            // Update the database record.
            int result = userDAO.update(user);

            // Query the user that was updated. Test that values match the updated values.
            Optional<User> oUser1 = userDAO.findById(user.getId());

            Assertions.assertTrue(oUser1.isPresent());

            User user1 = oUser1.get();

            Assertions.assertEquals(user1.getId(), user.getId());
            Assertions.assertEquals(user1.getUserId(), "ckramer");
            Assertions.assertEquals(user1.getFirstName(), "Cosmo");
            Assertions.assertEquals(user1.getLastName(), "Kramer");
            Assertions.assertEquals(user1.getPhoneNumber(), "+18385554321");
            Assertions.assertNotNull(user1.getUpdatedAt());

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteById() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);

            // Create test Users.
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

            user = (userDAO.save(user)).get();

            // Test delete fail response.
            int resFail = userDAO.deleteById(Long.valueOf(0));

            Assertions.assertEquals(0, resFail);

            // Test delete success response.
            int resSuccess = userDAO.deleteById(Long.valueOf(user.getId()));

            Assertions.assertEquals(1, resSuccess);

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);

            // Create test Users.
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

            user = (userDAO.save(user)).get();

            // Test delete fail response.
            User failUser = new User();
            failUser.setId(Long.valueOf(0));
            int resFail = userDAO.delete(failUser);

            Assertions.assertEquals(0, resFail);

            // Test delete success response.
            int resSuccess = userDAO.delete(user);

            Assertions.assertEquals(1, resSuccess);

            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
