package com.wawa87.moneystack.service.system.transaction.dao;

import com.wawa87.moneystack.service.system.budget.dao.BudgetDAO;
import com.wawa87.moneystack.service.system.budget.dao.BudgetDAOImpl;
import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.db.PGUtil;
import com.wawa87.moneystack.service.system.month.dao.MonthDAO;
import com.wawa87.moneystack.service.system.month.dao.MonthDAOImpl;
import com.wawa87.moneystack.service.system.month.model.Month;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAOImpl;
import com.wawa87.moneystack.service.system.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImplTest {
    private Connection connection;
    private UserDAO userDAO;
    private BudgetDAO budgetDAO;
    private MonthDAO monthDAO;
    private TransactionDAO transactionDAO;

    @BeforeEach
    public void prepareConnection() {
//        try {
//            this.connection = PGUtil.getDataSource().getConnection();
//            this.connection.setAutoCommit(false);
//
//            this.userDAO = new UserDAOImpl(this.connection);
//            this.budgetDAO = new BudgetDAOImpl(this.connection);
//            this.monthDAO = new MonthDAOImpl(this.connection);
//            this.transactionDAO = new TransactionDAOImpl(this.connection);
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
//            user = this.userDAO.save(user).get();
//
//            Budget budget = new Budget();
//            budget.setName("Big Spender");
//            budget.setUserId(user.getId());
//
//            budget = this.budgetDAO.save(budget).get();
//
//            Month month = new Month();
//            month.setBudgetId(budget.getId());
//            month.setYear(Year.of(2026));
//            month.setMonth(java.time.Month.APRIL);
//            month = monthDAO.save(month).get();
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
    public void testCreate() {
        User user = userDAO.findByUsername("cosmo").get();
        Budget budget = budgetDAO.findByUsername(user.getUsername()).get(0);
        Month month = monthDAO.findByBudgetId(budget.getId()).get(0);

        Transaction transaction = new Transaction();
        transaction.setMonthId(month.getId());
        transaction.setAmount(BigDecimal.valueOf(153.66));
        transaction.setDescription("test expense");
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionDAO.save((transaction)).get();

        Assertions.assertTrue(transaction.getId() > 0);
    }

    @Test
    public void testFindById() {
        User user = userDAO.findByUsername("cosmo").get();
        Budget budget = budgetDAO.findByUsername(user.getUsername()).get(0);
        Month month = monthDAO.findByBudgetId(budget.getId()).get(0);

        Transaction transaction = new Transaction();
        transaction.setMonthId(month.getId());
        transaction.setAmount(BigDecimal.valueOf(153.66));
        transaction.setDescription("test expense");
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionDAO.save((transaction)).get();

        Transaction transaction1 = transactionDAO.findById(transaction.getId()).get();

        Assertions.assertEquals(transaction.getTimestamp(), transaction1.getTimestamp());
    }

    @Test
    public void testFindByMonthId() {
        User user = userDAO.findByUsername("cosmo").get();
        Budget budget = budgetDAO.findByUsername(user.getUsername()).get(0);
        Month month = monthDAO.findByBudgetId(budget.getId()).get(0);

        Transaction transaction = new Transaction();
        transaction.setMonthId(month.getId());
        transaction.setAmount(BigDecimal.valueOf(153.66));
        transaction.setDescription("test expense");
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionDAO.save((transaction)).get();

        Transaction transaction1 = new Transaction();
        transaction1.setMonthId(month.getId());
        transaction1.setAmount(BigDecimal.valueOf(-27.99));
        transaction1.setDescription("test income");
        transaction1.setTimestamp(LocalDateTime.now());

        transaction1 = transactionDAO.save((transaction1)).get();

        List<Transaction> transactions = transactionDAO.findByMonthId(month.getId());

        Assertions.assertEquals(2, transactions.size());
    }

    @Test
    public void testUpdate() {
        User user = userDAO.findByUsername("cosmo").get();
        Budget budget = budgetDAO.findByUsername(user.getUsername()).get(0);
        Month month = monthDAO.findByBudgetId(budget.getId()).get(0);

        Transaction transaction = new Transaction();
        transaction.setMonthId(month.getId());
        transaction.setAmount(BigDecimal.valueOf(153.66));
        transaction.setDescription("test expense");
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionDAO.save((transaction)).get();

        transaction.setAmount(BigDecimal.valueOf(69.69));

        transactionDAO.update(transaction);

        Transaction transaction1 = transactionDAO.findById(transaction.getId()).get();

        Assertions.assertEquals(transaction.getAmount(), transaction1.getAmount());
    }

    @Test
    public void testDeleteById() {
        User user = userDAO.findByUsername("cosmo").get();
        Budget budget = budgetDAO.findByUsername(user.getUsername()).get(0);
        Month month = monthDAO.findByBudgetId(budget.getId()).get(0);

        Transaction transaction = new Transaction();
        transaction.setMonthId(month.getId());
        transaction.setAmount(BigDecimal.valueOf(153.66));
        transaction.setDescription("test expense");
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionDAO.save((transaction)).get();

        transactionDAO.deleteById(transaction.getId());

        List<Transaction> transactions = transactionDAO.findByMonthId(month.getId());

        Assertions.assertEquals(0, transactions.size());
    }

    @Test
    public void testDelete() {
        User user = userDAO.findByUsername("cosmo").get();
        Budget budget = budgetDAO.findByUsername(user.getUsername()).get(0);
        Month month = monthDAO.findByBudgetId(budget.getId()).get(0);

        Transaction transaction = new Transaction();
        transaction.setMonthId(month.getId());
        transaction.setAmount(BigDecimal.valueOf(153.66));
        transaction.setDescription("test expense");
        transaction.setTimestamp(LocalDateTime.now());

        transaction = transactionDAO.save((transaction)).get();

        transactionDAO.delete(transaction);

        List<Transaction> transactions = transactionDAO.findByMonthId(month.getId());

        Assertions.assertEquals(0, transactions.size());
    }
}
