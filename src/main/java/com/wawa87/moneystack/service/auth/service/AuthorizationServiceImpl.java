package com.wawa87.moneystack.service.auth.service;

import com.wawa87.moneystack.service.system.budget.dao.BudgetDAO;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAO;
import com.wawa87.moneystack.service.system.month.dao.MonthDAO;
import com.wawa87.moneystack.service.system.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.service.system.transaction.dao.TransactionDAO;
import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.model.User;
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

                if (requester.getId() == requestedUser.getId()) return true;
            }
        }

        return false;
    }

    @Override
    public boolean authorizeForCategory(Long requesterId, Long objectId) {
        return false;
    }

    @Override
    public boolean authorizeForSubcategory(Long requesterId, Long subcategoryId) {
        return false;
    }

    @Override
    public boolean authorizeForBudget(Long requesterId, Long budgetId) {
        return false;
    }

    @Override
    public boolean authorizeForMonth(Long requesterId, Long monthId) {
        return false;
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
