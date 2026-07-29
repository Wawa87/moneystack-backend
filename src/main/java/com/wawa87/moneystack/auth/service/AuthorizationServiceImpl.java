package com.wawa87.moneystack.auth.service;

import com.wawa87.moneystack.budget.dao.BudgetDAO;
import com.wawa87.moneystack.budget.model.Budget;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.month.dao.MonthDAO;
import com.wawa87.moneystack.month.model.Month;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.model.Subcategory;
import com.wawa87.moneystack.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.user.dao.UserDAO;
import com.wawa87.moneystack.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthorizationServiceImpl implements AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServiceImpl.class);
    private UserDAO userDAO;
    private CategoryDAO categoryDAO;
    private SubcategoryDAO subcategoryDAO;
    private MonthDAO monthDAO;
    private BudgetDAO budgetDAO;
    private TransactionDAO transactionDAO;

    public AuthorizationServiceImpl(UserDAO userDAO, CategoryDAO categoryDAO, SubcategoryDAO subcategoryDAO, BudgetDAO budgetDAO, MonthDAO monthDAO, TransactionDAO transactionDAO) {
        this.userDAO = userDAO;
        this.categoryDAO = categoryDAO;
        this.subcategoryDAO = subcategoryDAO;
        this.monthDAO = monthDAO;
        this.budgetDAO = budgetDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public boolean authorizeForUser(Long requesterId, Long userId) {
        // Check if requester is an admin.
        Optional<User> userOpt = this.userDAO.findById(requesterId);
        if (userOpt.isPresent() && (
                userOpt.get().getUsername().equals("dev") ||
                userOpt.get().getUsername().equals("admin") ||
                userOpt.get().getUsername().equals("administrator")
        )) return true;

        // Check if requester is the owner.
        userOpt = this.userDAO.findById(requesterId);
        if (userOpt.isPresent()) {
            User requester = userOpt.get();

            userOpt = this.userDAO.findById(userId);
            if (userOpt.isPresent()) {
                User requestedUser = userOpt.get();

                return requester.getId() == requestedUser.getId();
            }
        }

        return false;
    }

    @Override
    public boolean authorizeForCategory(Long requesterId, Long objectId) {
        if (this.isAdminRole(requesterId)) return true; // Allow admin.
        Optional<Category> categoryOpt = categoryDAO.findById(objectId);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();

            return category.getUserId() == requesterId;
        }
        return false;
    }

    @Override
    public boolean authorizeForCategory(Long requesterId, Category category) {
        if (this.isAdminRole(requesterId)) return true; // Allow admin.
        return requesterId == category.getUserId();
    }

    @Override
    public boolean authorizeForSubcategory(Long requesterId, Long subcategoryId) {
        if (this.isAdminRole(requesterId)) return true; // Allow admin.
        Optional<Subcategory> subcategoryOpt = subcategoryDAO.findById(subcategoryId);
        if (subcategoryOpt.isPresent()) {
            Subcategory subcategory = subcategoryOpt.get();

            Optional<Category> categoryOpt = categoryDAO.findById(subcategory.getCategoryId());
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();

                return category.getUserId() == requesterId;
            }
        }
        return false;
    }

    @Override
    public boolean authorizeForBudget(Long requesterId, Long budgetId) throws NotFoundException {
        // Authorize admin.
        if (isAdminRole(requesterId)) return true;

        // Get User to check.
        Optional<User> userOpt = userDAO.findById(requesterId);
        if (userOpt.isEmpty()) return false;

        // Get Budget to check.
        Optional<Budget> budgetOpt = budgetDAO.findById(budgetId);
        if (budgetOpt.isEmpty()) throw new NotFoundException();

        // Authorize User.
        return budgetOpt.get().getUserId() == userOpt.get().getId();
    }

    @Override
    public boolean authorizeForMonth(Long requesterId, Long monthId) throws NotFoundException {
        // Authorize admin.
        if (isAdminRole(requesterId)) return true;

        // Get User to check.
        Optional<User> userOpt = userDAO.findById(requesterId);
        if (userOpt.isEmpty()) return false;

        // Get Month to check.
        Optional<Month> monthOpt = this.monthDAO.findById(monthId);
        if (monthOpt.isEmpty()) throw new NotFoundException();

        // Get Budget to check.
        Optional<Budget> budgetOpt = budgetDAO.findById(monthOpt.get().getBudgetId());
        if (budgetOpt.isEmpty()) throw new NotFoundException();

        // Authorize User.
        return budgetOpt.get().getUserId() == userOpt.get().getId();
    }

    @Override
    public boolean authorizeForTransaction(Long requesterId, Long transactionId) {
        return false;
    }

    @Override
    public boolean isAdminRole(Long requesterId) {
        // Check if requester is an admin.
        Optional<User> userOpt = this.userDAO.findById(requesterId);
        if (userOpt.isPresent() && (
                userOpt.get().getUsername().equals("dev") ||
                        userOpt.get().getUsername().equals("admin") ||
                        userOpt.get().getUsername().equals("administrator")
        )) return true;
        return false;
    }

    @Override
    public boolean isAdminRole(String requesterUsername) {
        if (requesterUsername.equals("dev") ||
            requesterUsername.equals("admin") ||
            requesterUsername.equals("administrator")
        ) return true;
        return false;
    }
}
