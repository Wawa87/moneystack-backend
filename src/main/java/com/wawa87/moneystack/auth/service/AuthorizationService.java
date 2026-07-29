package com.wawa87.moneystack.auth.service;

import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.exceptions.NotFoundException;

public interface AuthorizationService {
    public boolean authorizeForUser(Long requesterId, Long userId);

    public boolean authorizeForCategory(Long requesterId, Long categoryId);
    public boolean authorizeForCategory(Long requesterId, Category category);

    public boolean authorizeForSubcategory(Long requesterId, Long subcategoryId);
    public boolean authorizeForBudget(Long requesterId, Long budgetId) throws NotFoundException;
    public boolean authorizeForMonth(Long requesterId, Long monthId) throws NotFoundException;
    public boolean authorizeForTransaction(Long requesterId, Long transactionId);

    public boolean isAdminRole(Long requesterId);
    public boolean isAdminRole(String requesterUsername);
}
