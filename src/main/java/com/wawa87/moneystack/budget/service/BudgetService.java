package com.wawa87.moneystack.budget.service;

import com.wawa87.moneystack.budget.model.Budget;
import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;

import java.util.List;

public interface BudgetService {
    public Budget save(Long requesterId, Budget budget) throws BadRequestException;
    public List<Budget> getAll(Long requesterId);
    public Budget findById(Long requesterId, Long budgetId) throws ValidationException, NotFoundException, AuthorizationException;
    public Budget update(Long requesterId, Long budgetId, Budget budget) throws AuthorizationException, NotFoundException, BadRequestException;
    public void delete(Long requesterId, Long budgetId) throws AuthorizationException, NotFoundException, BadRequestException;
    public void setActive(Long requesterId, Long budgetId) throws ValidationException, NotFoundException, BadRequestException;
    public Budget getActive(Long requesterId) throws NotFoundException;
}
