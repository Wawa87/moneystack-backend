package com.wawa87.moneystack.service.system.transaction;

import com.wawa87.moneystack.App;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDTO;
import com.wawa87.moneystack.service.system.transaction.model.Transaction;
import com.wawa87.moneystack.service.system.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private TransactionDAO transactionDAO;

    public TransactionService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
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
            transactionDTO.setCategory(App.categoryService.getCategoryDTOById(it.getCategoryId()).getName());
            transactionDTO.setSubcategory(App.subcategoryService.getSubcategoryDTOById(it.getSubcategoryId()).getName());
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
