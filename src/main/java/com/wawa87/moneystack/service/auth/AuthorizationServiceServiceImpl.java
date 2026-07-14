package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.service.system.user.dao.UserDAO;
import com.wawa87.moneystack.service.system.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthorizationServiceServiceImpl implements AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServiceServiceImpl.class);
    private UserDAO userDAO;

    public AuthorizationServiceServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public boolean authorizeForUser(Long requesterId, Long objectId) {
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

            userOpt = this.userDAO.findById(objectId);
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
    public boolean authorizeForSubcategory(Long requesterId, Long objectId) {
        return false;
    }
}
