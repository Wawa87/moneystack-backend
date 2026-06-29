package com.wawa87.moneystack.service.system.category.dao;

import com.wawa87.moneystack.service.system.category.model.Category;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAOImplTest {
    private Connection connection;

    @BeforeEach
    public void prepareConnection() {
        try {
            this.connection = PGUtil.getDataSource().getConnection();
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void endConnection() {
        try {
            this.connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSave() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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
        category.setName("TestCat");

        Optional<Category> categoryRs = categoryDAO.save(category);
        category = categoryRs.get();

        Assertions.assertNotNull(category.getId());
        Assertions.assertEquals(user.getId(), category.getUserId());
        Assertions.assertEquals("TestCat", category.getName());
    }

    @Test
    public void testFindById() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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
        category.setName("TestCat");

        Optional<Category> categoryRs = categoryDAO.save(category);
        category = categoryRs.get();

        categoryRs = categoryDAO.findById(category.getId());
        Category verifyCategory = categoryRs.get();

        Assertions.assertEquals(category.getId(), verifyCategory.getId());
        Assertions.assertEquals(category.getUserId(), verifyCategory.getUserId());
        Assertions.assertEquals(category.getName(), verifyCategory.getName());
    }

    @Test
    public void testFindByName() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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
        category.setName("Salaried Income");

        Optional<Category> categoryRs = categoryDAO.save(category);
        category = categoryRs.get();

        // Add category2
        Category category2 = new Category();
        category2.setUserId(user.getId());
        category2.setName("Dividend income");

        categoryRs = categoryDAO.save(category2);
        category2 = categoryRs.get();

        // Add category3
        Category category3 = new Category();
        category3.setUserId(user.getId());
        category3.setName("Crypto");

        categoryRs = categoryDAO.save(category3);
        category3 = categoryRs.get();

        List<Category> categories = categoryDAO.findByName("income");

        Assertions.assertEquals(2, categories.size());
        Assertions.assertEquals("Salaried Income", categories.get(0).getName());
        Assertions.assertEquals("Dividend income", categories.get(1).getName());
    }

    @Test
    public void testFindbyUserId() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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
        category.setName("TestCat");

        Optional<Category> categoryRs = categoryDAO.save(category);
        category = categoryRs.get();

        // Add category2
        Category category2 = new Category();
        category2.setUserId(user.getId());
        category2.setName("TestCat2");

        categoryRs = categoryDAO.save(category2);
        category2 = categoryRs.get();

        // Add category3
        Category category3 = new Category();
        category3.setUserId(user.getId());
        category3.setName("TestCat3");

        categoryRs = categoryDAO.save(category3);
        category3 = categoryRs.get();

        List<Category> categories = categoryDAO.findByUserId(user.getId());

        Assertions.assertEquals(3, categories.size());
        Assertions.assertEquals("TestCat", categories.get(0).getName());
        Assertions.assertEquals("TestCat2", categories.get(1).getName());
        Assertions.assertEquals("TestCat3", categories.get(2).getName());
    }

    @Test
    public void testFindByNameAndUserId() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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
        category.setName("Salaried Income");

        Optional<Category> categoryRs = categoryDAO.save(category);
        category = categoryRs.get();

        // Add category2
        Category category2 = new Category();
        category2.setUserId(user.getId());
        category2.setName("Dividend income");

        categoryRs = categoryDAO.save(category2);
        category2 = categoryRs.get();

        // Add category3
        Category category3 = new Category();
        category3.setUserId(user.getId());
        category3.setName("Crypto");

        categoryRs = categoryDAO.save(category3);
        category3 = categoryRs.get();

        List<Category> categories = categoryDAO.findByNameAndUserId("income", user.getId());

        Assertions.assertEquals(2, categories.size());
        Assertions.assertEquals("Salaried Income", categories.get(0).getName());
        Assertions.assertEquals("Dividend income", categories.get(1).getName());
    }

    @Test
    public void testUpdate() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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
        category.setName("TestCat");

        Optional<Category> categoryRs = categoryDAO.save(category);
        category = categoryRs.get();

        category.setName("UpdatedTestCat");

        categoryRs = categoryDAO.save(category);
        Category verifyCategory = categoryRs.get();

        Assertions.assertEquals("UpdatedTestCat", verifyCategory.getName());
    }

    @Test
    public void testUpdate2() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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

        // Test Category update.
        Category category = new Category();
        category.setUserId(user.getId());
        category.setName("TestCat");
        category = categoryDAO.save(category).get();

        int resultSuccess = categoryDAO.update(category);

        category.setId(Long.valueOf(000));

        int resultFailed = categoryDAO.update(category);

        System.out.println("Pause...");
    }

    @Test
    public void testDeleteById() {
        UserDAOImpl userDAO = new UserDAOImpl(this.connection);
        CategoryDAOImpl categoryDAO = new CategoryDAOImpl(this.connection);

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
        category.setName("TestCat");

        Optional<Category> categoryRs = categoryDAO.save(category);
        category = categoryRs.get();

        Assertions.assertNotNull(category.getId());

        int res = categoryDAO.deleteById(category.getId());
        Assertions.assertEquals(1, res);

        Optional<Category> categoryRs2 = categoryDAO.findById(category.getId());
        Assertions.assertTrue(categoryRs2.isEmpty());
    }
}