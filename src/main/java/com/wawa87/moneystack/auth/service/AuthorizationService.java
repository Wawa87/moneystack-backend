package com.wawa87.moneystack.auth.service;

import com.wawa87.moneystack.category.model.Category;

public interface AuthorizationService {
    public boolean authorizeForUser(Long requesterId, Long userId);

    public boolean authorizeForCategory(Long requesterId, Long categoryId);
    public boolean authorizeForCategory(Long requesterId, Category category);

    public boolean authorizeForSubcategory(Long requesterId, Long subcategoryId);
    public boolean authorizeForBudget(Long requesterId, Long budgetId);
    public boolean authorizeForMonth(Long requesterId, Long monthId);
    public boolean authorizeForTransaction(Long requesterId, Long transactionId);

    public boolean isAdminRole(Long requesterId);
    public boolean isAdminRole(String requesterUsername);
}
