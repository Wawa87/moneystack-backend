package com.wawa87.moneystack.service.page.model;

import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAOImpl;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Dashboard {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDAOImpl.class);

    private User user;
    private Budget budget;
    private List<Transaction> transactions;

    private UserService userService;

    public void loadDashboard() {

    }
}
