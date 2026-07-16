package com.wawa87.moneystack.service.auth.servlet;

import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.AppContext;
import com.wawa87.moneystack.service.auth.model.UsernameValidationRequest;
import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.user.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsernameValidationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private AppContext ctx;
    private UserService userService;

    public UsernameValidationServlet(AppContext ctx) {
        this.ctx = ctx;
        this.userService = this.ctx.getUserService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /validateUsername
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        try {
            UsernameValidationRequest usernameValidationRequest = ServletUtility.gson.fromJson(request.getReader(), UsernameValidationRequest.class);
            this.userService.validateNewUsername(usernameValidationRequest.getUsername());
            ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "Username is available.");
        } catch (JsonSyntaxException e) {
            ServletUtility.sendInternalError(response, e);
            return;
        } catch (NumberFormatException e) {
            ServletUtility.sendBadRequest(response);
            return;
        } catch (Exception e) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }
}
