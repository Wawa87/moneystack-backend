package com.wawa87.moneystack.service.system.transaction.dao;

import com.wawa87.moneystack.service.system.month.model.Month;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    // Create
    Optional<Transaction> save(Transaction transaction);

    // Read
    Optional<Transaction> findById(Long id);
    List<Transaction> findByMonthId(Long monthId);

    // Update
    int update(Transaction transaction);

    // Delete
    int deleteById(Long id);
    int delete(Transaction transaction);
}
