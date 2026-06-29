package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.service.system.category.model.Category;

public class AuthorizationChecker {
    public static boolean authorizeCategory(Category category, Long userId) {
        return (category.getUserId().equals(userId));
    }
}
