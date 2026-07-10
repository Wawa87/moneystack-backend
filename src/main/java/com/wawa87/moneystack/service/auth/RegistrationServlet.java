package com.wawa87.moneystack.service.auth;

import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.model.UserRequest;
import com.wawa87.moneystack.service.system.user.model.UserResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private UserService userService;

    public RegistrationServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /register
        try {
            UserRequest userRequest = ServletUtility.gson.fromJson(request.getReader(), UserRequest.class);
            UserResponse userResponse = userService.saveNewUser(userRequest);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_CREATED, userResponse);
            return;
        } catch (Exception e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }
}
