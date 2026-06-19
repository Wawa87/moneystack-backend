package com.wawa87.moneystack.service.page.model;

import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;
import com.wawa87.moneystack.service.system.user.model.User;

import java.util.List;

public class Dashboard {
    private User user;
    private Budget budget;
    private List<Transaction> transactions;
}
