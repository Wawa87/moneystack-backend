package com.wawa87.moneystack.service.system.month.dao;

import com.wawa87.moneystack.service.system.budget.dao.BudgetDAO;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.service.system.category.model.Category;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.month.model.Month;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAOImpl;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Optional;

public class MonthDAOImplTest {
    private Connection connection;
    private UserDAO userDAO;
    private BudgetDAO budgetDAO;
    private MonthDAO monthDAO;

    @BeforeEach
    public void prepareConnection() {
        try {
            this.connection = PGUtil.getDataSource().getConnection();
            this.connection.setAutoCommit(false);

            this.userDAO = new UserDAOImpl(this.connection);
            this.budgetDAO = new BudgetDAOImpl(this.connection);
            this.monthDAO = new MonthDAOImpl(this.connection);

            // Load the test data.
            User user = new User();
            user.setUsername("cosmo");

            ArrayList<String> emails = new ArrayList<>();
            emails.add("cosmo@seinfeld.com");
            emails.add("cosmo@kramerica.com");
            user.setEmails(emails);

            user.setFirstName("Cosmo");
            user.setLastName("Kramer");
            user.setPhoneNumber("+16195554321");

            user = this.userDAO.save(user).get();

            Budget budget0 = new Budget();
            budget0.setName("Big Spender");
            budget0.setUserId(user.getId());

            budget0 = this.budgetDAO.save(budget0).get();
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
        Budget budget = this.budgetDAO.findByUsername(user.getUsername()).get(0);

        Month month0 = new Month();
        month0.setBudgetId(budget.getId());
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.of(3));
        month0.setMonth(java.time.Month.MARCH);

        month0 = this.monthDAO.save(month0).get();

        Assertions.assertTrue(month0.getId() > 0);
    }
}
