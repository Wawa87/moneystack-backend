package com.wawa87.moneystack.transaction.service;

import com.wawa87.moneystack.category.service.CategoryService;
import com.wawa87.moneystack.subcategory.service.SubcategoryService;
import com.wawa87.moneystack.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.transaction.dao.TransactionDTO;
import com.wawa87.moneystack.transaction.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private TransactionDAO transactionDAO;
    private CategoryService categoryService;
    private SubcategoryService subcategoryService;

    public TransactionService(TransactionDAO transactionDAO, CategoryService categoryService, SubcategoryService subcategoryService) {
        this.transactionDAO = transactionDAO;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
    }

    public List<TransactionDTO> getTransactionDTOs(List<Transaction> transactions) {
        List<TransactionDTO> transactionDTOs = new ArrayList<>();
        transactions.forEach((it) -> {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setId(it.getId());
            transactionDTO.setMonthId(it.getMonthId());
            transactionDTO.setAmount(it.getAmount());
            transactionDTO.setDescription(it.getDescription());
            transactionDTO.setTimestamp(it.getTimestamp());
            transactionDTO.setCategory(this.categoryService.findCategoryById(it.getCategoryId()).getName());
            transactionDTO.setSubcategory(this.subcategoryService.getSubcategoryDTOById(it.getSubcategoryId()).getName());
            transactionDTOs.add(transactionDTO);
        });
        return transactionDTOs;
    }

    public List<TransactionDTO> getTransactionDTOsByMonthId(Long monthId) {
        List<Transaction> transactions = transactionDAO.findByMonthId(monthId);
        return getTransactionDTOs(transactions);
    }

    public void saveTransaction(Transaction transaction) {
        transactionDAO.save(transaction);
    }
}
