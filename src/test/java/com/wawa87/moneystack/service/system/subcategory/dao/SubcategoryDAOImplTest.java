package com.wawa87.moneystack.service.system.subcategory.dao;

import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.model.Subcategory;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SubcategoryDAOImplTest {
    private Connection connection;
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private SubcategoryDAO subcategoryDAO;

    @BeforeEach
    public void prepareConnection() {
//        try {
//            this.connection = PGUtil.getDataSource().getConnection();
//            this.connection.setAutoCommit(false);
//
//            // Load required User and Category data.
//            this.userDAO = new UserDAOImpl(this.connection);
//            this.categoryDAO = new CategoryDAOImpl(this.connection);
//            this.subcategoryDAO = new SubcategoryDAOImpl(this.connection);
//
//            // Create the test User.
//            User user = new User();
//            user.setUsername("cosmo");
//
//            ArrayList<String> emails = new ArrayList<>();
//            emails.add("cosmo@seinfeld.com");
//            emails.add("cosmo@kramerica.com");
//            user.setEmails(emails);
//
//            user.setFirstName("Cosmo");
//            user.setLastName("Kramer");
//            user.setPhoneNumber("+16195554321");
//
//            // Insert the test User and confirm id generation of new record.
//            Optional<User> rsUser = this.userDAO.save(user);
//            user = rsUser.get();
//
//            Category category = new Category();
//            category.setUserId(user.getId());
//            category.setName("Housing");
//
//            Optional<Category> categoryRs = this.categoryDAO.save(category);
//            category = categoryRs.get();
//
//            Category category2 = new Category();
//            category2.setUserId(user.getId());
//            category2.setName("Entertainment");
//
//            categoryRs = this.categoryDAO.save(category2);
//            category2 = categoryRs.get();
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
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
        User user = this.userDAO.findByUsername("cosmo").get();
        Category category0 = this.categoryDAO.findByName("entertainment").get(0);
        Category category1 = this.categoryDAO.findByName("hous").get(0);

        Subcategory subcategory0 = new Subcategory();
        subcategory0.setName("Mortgage");
        subcategory0.setCategoryId(category0.getId());
        subcategory0 = this.subcategoryDAO.save(subcategory0).get();

        Subcategory subcategory1 = new Subcategory();
        subcategory1.setName("Netflix");
        subcategory1.setCategoryId(category1.getId());
        subcategory1 = this.subcategoryDAO.save(subcategory1).get();

        Assertions.assertNotNull(subcategory0.getId());
        Assertions.assertNotNull(subcategory1.getId());
    }

    @Test
    public void testFindById() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Category category0 = this.categoryDAO.findByName("entertainment").get(0);

        Subcategory subcategory0 = new Subcategory();
        subcategory0.setName("Mortgage");
        subcategory0.setCategoryId(category0.getId());
        subcategory0 = this.subcategoryDAO.save(subcategory0).get();

        Subcategory subcategory1 = this.subcategoryDAO.findById(subcategory0.getId()).get();

        Assertions.assertEquals(subcategory0.getName(), subcategory1.getName());
    }

    @Test
    public void testFindByCategoryId() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Category category0 = this.categoryDAO.findByName("entertainment").get(0);

        Subcategory subcategory0 = new Subcategory();
        subcategory0.setName("Netflix");
        subcategory0.setCategoryId(category0.getId());
        subcategory0 = this.subcategoryDAO.save(subcategory0).get();

        Subcategory subcategory1 = new Subcategory();
        subcategory1.setName("Paramount");
        subcategory1.setCategoryId(category0.getId());
        subcategory1 = this.subcategoryDAO.save(subcategory0).get();

        List<Subcategory> subcategories = subcategoryDAO.findByCategoryId(category0.getId());

        Assertions.assertEquals(2, subcategories.size());
    }

    @Test
    public void testUpdate() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Category category0 = this.categoryDAO.findByName("entertainment").get(0);

        Subcategory subcategory0 = new Subcategory();
        subcategory0.setName("Netflix");
        subcategory0.setCategoryId(category0.getId());
        subcategory0 = this.subcategoryDAO.save(subcategory0).get();

        subcategory0.setName("Paramount");

        this.subcategoryDAO.update(subcategory0);

        Subcategory subcategory1 = subcategoryDAO.findById(subcategory0.getId()).get();

        Assertions.assertEquals("Paramount", subcategory1.getName());
    }

    @Test
    public void testDeleteById() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Category category0 = this.categoryDAO.findByName("entertainment").get(0);

        Subcategory subcategory0 = new Subcategory();
        subcategory0.setName("Netflix");
        subcategory0.setCategoryId(category0.getId());
        subcategory0 = this.subcategoryDAO.save(subcategory0).get();

        Subcategory subcategory1 = new Subcategory();
        subcategory1.setName("Paramount");
        subcategory1.setCategoryId(category0.getId());
        subcategory1 = this.subcategoryDAO.save(subcategory0).get();

        List<Subcategory> subcategories = subcategoryDAO.findByCategoryId(category0.getId());

        Assertions.assertEquals(2, subcategories.size());

        subcategoryDAO.deleteById(subcategory0.getId());

        subcategories = subcategoryDAO.findByCategoryId(category0.getId());

        Assertions.assertEquals(1, subcategories.size());
    }
}
