package com.wawa87.moneystack.user.servlet;

import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.user.service.UserService;
import com.wawa87.moneystack.user.model.UserRequest;
import com.wawa87.moneystack.user.model.UserResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private AppContext ctx;
    private UserService userService;

    public RegistrationServlet(AppContext ctx) {
        this.ctx = ctx;
        this.userService = this.ctx.getUserService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /register
        try {
            UserRequest userRequest = ServletUtility.gson.fromJson(request.getReader(), UserRequest.class);
            UserResponse userResponse = userService.register(userRequest);
            ServletUtility.sendResponseObject(response, HttpServletResponse.SC_CREATED, userResponse);
            return;
        } catch (Exception e) {
            ServletUtility.sendInternalError(response, e);
            return;
        }
    }
}
