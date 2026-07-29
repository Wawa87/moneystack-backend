package com.wawa87.moneystack.budget.service;

import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.budget.dao.BudgetDAO;
import com.wawa87.moneystack.budget.model.Budget;
import com.wawa87.moneystack.common.exceptions.AuthorizationException;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class BudgetServiceImpl implements BudgetService {
    private static final Logger logger = LoggerFactory.getLogger(BudgetServiceImpl.class);

    private BudgetDAO budgetDAO;
    private AuthorizationService authorizationService;

    public BudgetServiceImpl(BudgetDAO budgetDAO, AuthorizationService authorizationService) {
        this.budgetDAO = budgetDAO;
        this.authorizationService = authorizationService;
    }

    @Override
    public Budget save(Long requesterId, Budget budget) throws BadRequestException {
        // Validate Budget values.
        if (budget.getName().isBlank()) throw new BadRequestException("Budget name is invalid.");

        // Set the User Id.
        budget.setUserId(requesterId);

        // Save the new Budget.
        Optional<Budget> budgetOpt = budgetDAO.save(budget);
        if (budgetOpt.isEmpty()) throw new BadRequestException("Budget failed to save.");
        else return budgetOpt.get();
    }

    @Override
    public List<Budget> getAll(Long requesterId) {
        return budgetDAO.findByUserId(requesterId);
    }

    @Override
    public Budget findById(Long requesterId, Long budgetId) throws ValidationException, NotFoundException, AuthorizationException {
        // Authorize admin.
        if (!authorizationService.authorizeForBudget(requesterId, budgetId)) throw new AuthorizationException();

        // Get the Budget.
        Optional<Budget> budgetOpt = budgetDAO.findById(budgetId);
        if (budgetOpt.isEmpty()) throw new NotFoundException();
        else return budgetOpt.get();
    }

    @Override
    public Budget update(Long requesterId, Long budgetId, Budget budget) throws AuthorizationException, NotFoundException, BadRequestException {
        // Validate new Budget values.
        if (budget.getName().isBlank()) throw new BadRequestException("Budget name is invalid.");

        // Authorize admin.
        if (!authorizationService.authorizeForBudget(requesterId, budgetId)) throw new AuthorizationException();

        // Get the Budget to update.
        Optional<Budget> budgetOpt = budgetDAO.findById(budgetId);
        if (budgetOpt.isEmpty()) throw new NotFoundException();

        Budget updateBudget = budgetOpt.get();
        updateBudget.setName(budget.getName());

        // Update the Budget.
        if (budgetDAO.update(updateBudget) != 1) throw new BadRequestException("Budget update failed.");
        else return updateBudget;
    }

    @Override
    public void delete(Long requesterId, Long budgetId) throws AuthorizationException, NotFoundException, BadRequestException {
        // Authorize admin.
        if (!authorizationService.authorizeForBudget(requesterId, budgetId)) throw new AuthorizationException();

        // Delete the Budget.
        if (budgetDAO.deleteById(budgetId) != 1) throw new BadRequestException("Budget delete failed.");
    }

    @Override
    public void setActive(Long requesterId, Long budgetId) throws ValidationException, NotFoundException, BadRequestException {
        // Authorize admin.
        if (!authorizationService.authorizeForBudget(requesterId, budgetId)) throw new ValidationException();

        // Get the Budget.
        Optional<Budget> budgetOpt = budgetDAO.findById(budgetId);
        if (budgetOpt.isEmpty()) throw new NotFoundException();

        // Update the Budget.
        Budget budget = budgetOpt.get();
        budget.setActive(true);
        int result = budgetDAO.update(budget);

        // Return result
        if (result != 1) throw new BadRequestException("Budget failed to update as Active.");
    }

    @Override
    public Budget getActive(Long requesterId) throws NotFoundException {
        Optional<Budget> budgetOpt = budgetDAO.findActiveByUserId(requesterId);
        if (budgetOpt.isEmpty()) throw new NotFoundException();
        else return budgetOpt.get();
    }
}
