package com.wawa87.moneystack.service.auth;

public interface AuthorizationService {
    public boolean authorizeForUser(Long requesterId, Long objectId);
    public boolean authorizeForCategory(Long requesterId, Long objectId);
    public boolean authorizeForSubcategory(Long requesterId, Long objectId);
}
