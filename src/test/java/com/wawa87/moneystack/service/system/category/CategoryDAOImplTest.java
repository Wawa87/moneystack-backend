package com.wawa87.moneystack.service.system.category;

import com.wawa87.moneystack.service.system.category.model.Category;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.user.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAOImplTest {
    @Test
    public void testSave() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);

            // Create the test User.
            User user = new User();
            user.setUsername("testuser");

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
            user = rsUser.get();

            Category category = new Category();
            category.setUserId(user.getId());
            category.setCategoryName("TestCat");

            Optional<Category> categoryRs = categoryDAO.save(category);
            category = categoryRs.get();

            Assertions.assertNotNull(category.getId());
            Assertions.assertNotNull(category.getCreatedAt());
            Assertions.assertEquals(user.getId(), category.getUserId());
            Assertions.assertEquals("TestCat", category.getCategoryName());

            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindById() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);

            // Create the test User.
            User user = new User();
            user.setUsername("testuser");

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
            user = rsUser.get();

            Category category = new Category();
            category.setUserId(user.getId());
            category.setCategoryName("TestCat");

            Optional<Category> categoryRs = categoryDAO.save(category);
            category = categoryRs.get();

            categoryRs = categoryDAO.findById(category.getId());
            Category verifyCategory = categoryRs.get();

            Assertions.assertEquals(category.getId(), verifyCategory.getId());
            Assertions.assertEquals(category.getUserId(), verifyCategory.getUserId());
            Assertions.assertEquals(category.getCategoryName(), verifyCategory.getCategoryName());
            Assertions.assertEquals(category.getCreatedAt(), verifyCategory.getCreatedAt());

            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindByName() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);

            // Create the test User.
            User user = new User();
            user.setUsername("testuser");

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
            user = rsUser.get();

            // Add category
            Category category = new Category();
            category.setUserId(user.getId());
            category.setCategoryName("Salaried Income");

            Optional<Category> categoryRs = categoryDAO.save(category);
            category = categoryRs.get();

            // Add category2
            Category category2 = new Category();
            category2.setUserId(user.getId());
            category2.setCategoryName("Dividend income");

            categoryRs = categoryDAO.save(category2);
            category2 = categoryRs.get();

            // Add category3
            Category category3 = new Category();
            category3.setUserId(user.getId());
            category3.setCategoryName("Crypto");

            categoryRs = categoryDAO.save(category3);
            category3 = categoryRs.get();

            List<Category> categories = categoryDAO.findByName("income");

            Assertions.assertEquals(2, categories.size());
            Assertions.assertEquals("Salaried Income", categories.get(0).getCategoryName());
            Assertions.assertEquals("Dividend income", categories.get(1).getCategoryName());

            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindbyUserId() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);

            // Create the test User.
            User user = new User();
            user.setUsername("testuser");

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
            user = rsUser.get();

            // Add category
            Category category = new Category();
            category.setUserId(user.getId());
            category.setCategoryName("TestCat");

            Optional<Category> categoryRs = categoryDAO.save(category);
            category = categoryRs.get();

            // Add category2
            Category category2 = new Category();
            category2.setUserId(user.getId());
            category2.setCategoryName("TestCat2");

            categoryRs = categoryDAO.save(category2);
            category2 = categoryRs.get();

            // Add category3
            Category category3 = new Category();
            category3.setUserId(user.getId());
            category3.setCategoryName("TestCat3");

            categoryRs = categoryDAO.save(category3);
            category3 = categoryRs.get();

            List<Category> categories = categoryDAO.findByUserId(user.getId());

            Assertions.assertEquals(3, categories.size());
            Assertions.assertEquals("TestCat", categories.get(0).getCategoryName());
            Assertions.assertEquals("TestCat2", categories.get(1).getCategoryName());
            Assertions.assertEquals("TestCat3", categories.get(2).getCategoryName());

            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFindByNameAndUserId() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);

            // Create the test User.
            User user = new User();
            user.setUsername("testuser");

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
            user = rsUser.get();

            // Add category
            Category category = new Category();
            category.setUserId(user.getId());
            category.setCategoryName("Salaried Income");

            Optional<Category> categoryRs = categoryDAO.save(category);
            category = categoryRs.get();

            // Add category2
            Category category2 = new Category();
            category2.setUserId(user.getId());
            category2.setCategoryName("Dividend income");

            categoryRs = categoryDAO.save(category2);
            category2 = categoryRs.get();

            // Add category3
            Category category3 = new Category();
            category3.setUserId(user.getId());
            category3.setCategoryName("Crypto");

            categoryRs = categoryDAO.save(category3);
            category3 = categoryRs.get();

            List<Category> categories = categoryDAO.findByNameAndUserId("income", user.getId());

            Assertions.assertEquals(2, categories.size());
            Assertions.assertEquals("Salaried Income", categories.get(0).getCategoryName());
            Assertions.assertEquals("Dividend income", categories.get(1).getCategoryName());

            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testUpdate() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);

            // Create the test User.
            User user = new User();
            user.setUsername("testuser");

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
            user = rsUser.get();

            Category category = new Category();
            category.setUserId(user.getId());
            category.setCategoryName("TestCat");

            Optional<Category> categoryRs = categoryDAO.save(category);
            category = categoryRs.get();

            category.setCategoryName("UpdatedTestCat");

            categoryRs = categoryDAO.save(category);
            Category verifyCategory = categoryRs.get();

            Assertions.assertEquals("UpdatedTestCat", verifyCategory.getCategoryName());

            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteById() {
        try (Connection connection = PGUtil.getDataSource().getConnection()) {
            connection.setAutoCommit(false);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);

            // Create the test User.
            User user = new User();
            user.setUsername("testuser");

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
            user = rsUser.get();

            Category category = new Category();
            category.setUserId(user.getId());
            category.setCategoryName("TestCat");

            Optional<Category> categoryRs = categoryDAO.save(category);
            category = categoryRs.get();

            Assertions.assertNotNull(category.getId());

            int res = categoryDAO.deleteById(category.getId());
            Assertions.assertEquals(1, res);

            Optional<Category> categoryRs2 = categoryDAO.findById(category.getId());
            Assertions.assertTrue(categoryRs2.isEmpty());

            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}