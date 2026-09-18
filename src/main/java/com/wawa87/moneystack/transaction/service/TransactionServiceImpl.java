package com.wawa87.moneystack.transaction.service;

import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.category.service.CategoryServiceImpl;
import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.subcategory.service.SubcategoryServiceImpl;
import com.wawa87.moneystack.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.transaction.dao.TransactionDTO;
import com.wawa87.moneystack.transaction.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private TransactionDAO transactionDAO;
    private CategoryServiceImpl categoryService;
    private SubcategoryServiceImpl subcategoryService;
    private AuthorizationService authorizationService;

    public TransactionServiceImpl(TransactionDAO transactionDAO, CategoryServiceImpl categoryService, SubcategoryServiceImpl subcategoryService, AuthorizationService authorizationService) {
        this.transactionDAO = transactionDAO;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
        this.authorizationService = authorizationService;
    }

    @Override
    public Transaction save(Long requesterId, Transaction transaction) throws BadRequestException, NotFoundException, AuthorizationException {
        // Validate Transaction values.
        if (transaction.getMonthId() == null || transaction.getMonthId() == 0) throw new BadRequestException("Invalid month id.");
        if (!this.authorizationService.authorizeForMonth(requesterId, transaction.getMonthId())) throw new AuthorizationException("Unauthorized for month id: " + transaction.getMonthId());
        if (transaction.getCategoryId() != null) {
            if (!this.authorizationService.authorizeForCategory(requesterId, transaction.getCategoryId())) throw new AuthorizationException("Invalid category id.");
        }
        if (transaction.getSubcategoryId() != null) {
            if (!this.authorizationService.authorizeForSubcategory(requesterId, transaction.getSubcategoryId())) throw new AuthorizationException("Invalid subcategory id.");
        }

        // Add current timestamp to Transaction if none is provided.
        if (transaction.getTimestamp() == null) transaction.setTimestamp(LocalDateTime.now());

        // Save the transaction.
        Optional<Transaction> transactionOpt = this.transactionDAO.save(transaction);
        if (transactionOpt.isEmpty()) throw new BadRequestException("Transaction failed to save.");
        else return transactionOpt.get();
    }

    @Override
    public List<Transaction> getAllByMonth(Long requesterId, Long monthId) throws AuthorizationException, NotFoundException {
        // Authorize
        if (!this.authorizationService.authorizeForMonth(requesterId, monthId)) throw new AuthorizationException();

        // Get transactions.
        List<Transaction> transactions = this.transactionDAO.findByMonthId(monthId);
        return transactions;
    }

    @Override
    public Transaction findById(Long requesterId, Long transactionId) throws AuthorizationException, NotFoundException {
        // Authorize
        if (!this.authorizationService.authorizeForTransaction(requesterId, transactionId)) throw new AuthorizationException("Unauthorized.");

        // Get the transaction
        Optional<Transaction> transactionOpt = this.transactionDAO.findById(transactionId);
        if (transactionOpt.isEmpty()) throw new NotFoundException();
        else return transactionOpt.get();
    }

    @Override
    public Transaction update(Long requesterId, Long transactionId, Transaction transaction) throws AuthorizationException, NotFoundException, BadRequestException {
        // Authorize.
        if (!authorizationService.authorizeForMonth(requesterId, transactionId)) throw new AuthorizationException();

        // Get the Transaction to update.
        Optional<Transaction> transactionOpt = this.transactionDAO.findById(transactionId);
        if (transactionOpt.isEmpty()) throw new NotFoundException();

        // Update the Transaction.
        Transaction updateTransaction = transactionOpt.get();
        updateTransaction.setCategoryId(transaction.getCategoryId());
        updateTransaction.setSubcategoryId(transaction.getSubcategoryId());
        updateTransaction.setDescription(transaction.getDescription());
        updateTransaction.setAmount(transaction.getAmount());
        updateTransaction.setTimestamp(transaction.getTimestamp());

        // Return result code. Success == 1, Error == 0.
        if (this.transactionDAO.update(updateTransaction) == 1) return updateTransaction;
        else throw new BadRequestException("Transaction update failed.");
    }

    @Override
    public void delete(Long requesterId, Long transactionId) throws AuthorizationException, BadRequestException {
        // Authorize.
        if (!this.authorizationService.authorizeForTransaction(requesterId, transactionId)) throw new AuthorizationException();

        // Delete the Transaction.
        if (this.transactionDAO.deleteById(transactionId) != 1) throw new BadRequestException("Failed to delete the Transaction.");
    }
}
