package com.wawa87.moneystack.service.system.budget.dao;

import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAOImplTest {
    private Connection connection;
    private UserDAO userDAO;
    private BudgetDAO budgetDAO;

    @BeforeEach
    public void prepareConnection() {
        try {
            this.connection = PGUtil.getDataSource().getConnection();
            this.connection.setAutoCommit(false);
            this.userDAO = new UserDAOImpl(this.connection);
            this.budgetDAO = new BudgetDAOImpl(this.connection);

            // Load test data.
            User user = new User();
            user.setUsername("cosmo");

            ArrayList<String> emails = new ArrayList<>();
            emails.add("cosmo@seinfeld.com");
            emails.add("cosmo@kramerica.com");
            user.setEmails(emails);

            user.setFirstName("Cosmo");
            user.setLastName("Kramer");
            user.setPhoneNumber("+16195554321");

            user = userDAO.save(user).get();
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
        User user = this.userDAO.findByUsername("cosmo").get();

        Budget budget0 = new Budget();
        budget0.setName("BigSaver");
        budget0.setUserId(user.getId());
        budget0 = this.budgetDAO.save(budget0).get();

        Budget budget1 = new Budget();
        budget1.setName("BigSpender");
        budget1.setUserId(user.getId());
        budget1 = this.budgetDAO.save(budget1).get();

        Assertions.assertTrue(budget0.getId() > 0);
        Assertions.assertTrue(budget1.getId() > 0);

        List<Budget> budgets = this.budgetDAO.findByUsername(user.getUsername());

        Assertions.assertEquals(2, budgets.size());
    }

    @Test
    public void testFindById() {
        User user = this.userDAO.findByUsername("cosmo").get();

        Budget budget0 = new Budget();
        budget0.setName("BigSaver");
        budget0.setUserId(user.getId());
        budget0 = this.budgetDAO.save(budget0).get();

        Budget budget1 = this.budgetDAO.findById(budget0.getId()).get();

        Assertions.assertEquals(budget0.getId(), budget1.getId());
        Assertions.assertEquals(budget0.getName(), budget1.getName());
    }

    @Test
    public void testUpdate() {
        User user = this.userDAO.findByUsername("cosmo").get();

        Budget budget0 = new Budget();
        budget0.setName("BigSaver");
        budget0.setUserId(user.getId());
        budget0 = this.budgetDAO.save(budget0).get();

        budget0.setName("Extreme Saver");
        this.budgetDAO.update(budget0);

        Budget budget1 = this.budgetDAO.findById(budget0.getId()).get();

        Assertions.assertEquals("Extreme Saver", budget1.getName());
    }

    @Test
    public void testDeletions() {
        User user = this.userDAO.findByUsername("cosmo").get();

        Budget budget0 = new Budget();
        budget0.setName("BigSaver");
        budget0.setUserId(user.getId());
        budget0 = this.budgetDAO.save(budget0).get();

        Budget budget1 = new Budget();
        budget1.setName("BigSpender");
        budget1.setUserId(user.getId());
        budget1 = this.budgetDAO.save(budget1).get();

        List<Budget> budgets = this.budgetDAO.findByUsername(user.getUsername());
        Assertions.assertEquals(2, budgets.size());

        this.budgetDAO.delete(budget0);
        this.budgetDAO.deleteById(budget1.getId());

        budgets = this.budgetDAO.findByUsername(user.getUsername());
        Assertions.assertEquals(0, budgets.size());
    }
}
