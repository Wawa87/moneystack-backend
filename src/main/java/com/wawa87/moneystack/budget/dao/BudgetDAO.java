package com.wawa87.moneystack.budget.dao;

import com.wawa87.moneystack.budget.model.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetDAO {
    // Create
    Optional<Budget> save(Budget budget);

    // Read
    Optional<Budget> findById(Long id);
    List<Budget> findByUsername(String username);
    List<Budget> findByUserId(Long userId);
    Optional<Budget> findActiveByUserId(Long userId);

    // Update
    int update(Budget budget);

    // Delete
    int deleteById(Long id);
    int delete(Budget budget);
}
