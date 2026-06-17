package com.wawa87.moneystack.service.system.month.dao;

import com.wawa87.moneystack.service.system.budget.model.Budget;
import com.wawa87.moneystack.service.system.month.model.Month;

import java.util.List;
import java.util.Optional;

public interface MonthDAO {
    // Create
    Optional<Month> save(Month month);

    // Read
    Optional<Month> findById(Long id);
    List<Month> findByBudgetId(Long budgetId);

    // Update
    int update(Month month);

    // Delete
    int deleteById(Long id);
    int delete(Month month);
}
