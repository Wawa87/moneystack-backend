package com.wawa87.moneystack.transaction.service;

import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.transaction.model.Transaction;

import java.util.List;

public interface TransactionService {
    public Transaction save(Long requesterId, Transaction transaction) throws BadRequestException, NotFoundException, AuthorizationException;
    public List<Transaction> getAllByMonth(Long requesterId, Long monthId) throws AuthorizationException, NotFoundException;
    public Transaction findById(Long requesterId, Long transactionId) throws AuthorizationException, NotFoundException;
    public Transaction update(Long requesterId, Long transactionId, Transaction transaction) throws AuthorizationException, NotFoundException, BadRequestException;
    public void delete(Long requesterId, Long transactionId) throws AuthorizationException, BadRequestException;
}
