package com.wawa87.moneystack.budget.service;

import com.wawa87.moneystack.budget.dao.BudgetDAO;
import com.wawa87.moneystack.budget.model.Budget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BudgetService {
    private static final Logger logger = LoggerFactory.getLogger(BudgetService.class);

    private BudgetDAO budgetDAO;

    public BudgetService(BudgetDAO budgetDAO) {
        this.budgetDAO = budgetDAO;
    }

    public void setBudgetAsActive(String username, Long budgetId) {
        // Get the full list of budgets. All will be needed to restrict active budgets to 1.
        List<Budget> budgets = budgetDAO.findByUsername(username);
        budgets.forEach(bud -> {
            if (bud.getId() == budgetId) {
                bud.setActive(Boolean.TRUE);
                budgetDAO.update(bud);
            } else {
                bud.setActive(Boolean.FALSE);
                budgetDAO.update(bud);
            }
        });
    }

    public List<Budget> getBudgetsForUser(String username) {
        List<Budget> budgets = budgetDAO.findByUsername(username);
        return budgets;
    }

    public void saveBudget(Budget budget) {
        budget = budgetDAO.save(budget).get();
    }
}
