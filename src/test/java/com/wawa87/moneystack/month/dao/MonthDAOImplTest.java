package com.wawa87.moneystack.month.dao;

import com.wawa87.moneystack.budget.dao.BudgetDAO;
import com.wawa87.moneystack.budget.model.Budget;
import com.wawa87.moneystack.month.dao.MonthDAO;
import com.wawa87.moneystack.month.model.Month;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

public class MonthDAOImplTest {
    private DataSource dataSource;
    private Connection connection;
    private UserDAO userDAO;
    private BudgetDAO budgetDAO;
    private MonthDAO monthDAO;

    @BeforeEach
    public void prepareConnection() {
//        try {
//            this.userDAO = new UserDAOImpl(this.dataSource);
//            this.budgetDAO = new BudgetDAOImpl(this.dataSource);
//            this.monthDAO = new MonthDAOImpl(this.dataSource);
//
//            // Load the test data.
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
//            user = this.userDAO.save(UserRequest.convertToUser(user)).get();
//
//            Budget budget0 = new Budget();
//            budget0.setName("Big Spender");
//            budget0.setUserId(user.getId());
//
//            budget0 = this.budgetDAO.save(budget0).get();
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
        Budget budget = this.budgetDAO.findByUsername(user.getUsername()).get(0);

        Month month0 = new Month();
        month0.setBudgetId(budget.getId());
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.of(3));
        month0.setMonth(java.time.Month.MARCH);

        month0 = this.monthDAO.save(month0).get();

        Assertions.assertTrue(month0.getId() > 0);
    }

    @Test
    public void testFindById() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Budget budget = this.budgetDAO.findByUsername(user.getUsername()).get(0);

        Month month0 = new Month();
        month0.setBudgetId(budget.getId());
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.of(3));
        month0.setMonth(java.time.Month.MARCH);

        month0 = this.monthDAO.save(month0).get();

        Month month1 = new Month();
        month1.setBudgetId(budget.getId());
        month1.setYear(Year.of(2026));
        month1.setMonth(java.time.Month.of(3));
        month1.setMonth(java.time.Month.MARCH);

        month1 = this.monthDAO.save(month1).get();

        Month month1test = monthDAO.findById(month1.getId()).get();

        Assertions.assertEquals(month1.getMonth(), month1test.getMonth());
    }

    @Test
    public void testFindByBudgetId() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Budget budget = this.budgetDAO.findByUsername(user.getUsername()).get(0);

        Month month0 = new Month();
        month0.setBudgetId(budget.getId());
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.of(3));
        month0.setMonth(java.time.Month.MARCH);

        month0 = this.monthDAO.save(month0).get();

        Month month1 = new Month();
        month1.setBudgetId(budget.getId());
        month1.setYear(Year.of(2026));
        month1.setMonth(java.time.Month.of(3));
        month1.setMonth(java.time.Month.MARCH);

        month1 = this.monthDAO.save(month1).get();

        List<Month> months = monthDAO.findByBudgetId(budget.getId());

        Assertions.assertEquals(2, months.size());
    }

    @Test
    public void testUpdate() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Budget budget = this.budgetDAO.findByUsername(user.getUsername()).get(0);

        Month month0 = new Month();
        month0.setBudgetId(budget.getId());
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.of(3));
        month0.setMonth(java.time.Month.MARCH);

        month0 = this.monthDAO.save(month0).get();

        month0.setMonth(java.time.Month.APRIL);
        monthDAO.update(month0);

        Month month0test = monthDAO.findById(month0.getId()).get();

        Assertions.assertEquals(java.time.Month.APRIL, month0test.getMonth());
    }

    @Test
    public void testDeleteById() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Budget budget = this.budgetDAO.findByUsername(user.getUsername()).get(0);

        Month month0 = new Month();
        month0.setBudgetId(budget.getId());
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.of(3));
        month0.setMonth(java.time.Month.MARCH);

        month0 = this.monthDAO.save(month0).get();

        monthDAO.deleteById(month0.getId());

        List<Month> months = monthDAO.findByBudgetId(budget.getId());

        Assertions.assertEquals(0, months.size());
    }

    @Test
    public void testDelete() {
        User user = this.userDAO.findByUsername("cosmo").get();
        Budget budget = this.budgetDAO.findByUsername(user.getUsername()).get(0);

        Month month0 = new Month();
        month0.setBudgetId(budget.getId());
        month0.setYear(Year.of(2026));
        month0.setMonth(java.time.Month.of(3));
        month0.setMonth(java.time.Month.MARCH);

        month0 = this.monthDAO.save(month0).get();

        monthDAO.delete(month0);

        List<Month> months = monthDAO.findByBudgetId(budget.getId());

        Assertions.assertEquals(0, months.size());
    }
}
