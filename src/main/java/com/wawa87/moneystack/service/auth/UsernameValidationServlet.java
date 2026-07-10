package com.wawa87.moneystack.service.auth;

import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.model.User;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UsernameValidationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private UserService userService;

    public UsernameValidationServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Handle request: /validateUsername
        Long userId = Long.parseLong(String.valueOf(request.getAttribute("userId")));

        try {
            UsernameValidationRequest usernameValidationRequest = ServletUtility.gson.fromJson(request.getReader(), UsernameValidationRequest.class);
            UsernameValidationResponse usernameValidationResponse = this.userService.validateNewUsername(usernameValidationRequest.getUsername());

            if (usernameValidationResponse.getResult()) {
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, usernameValidationResponse);
                return;
            } else {
                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, usernameValidationResponse);
                return;
            }
        } catch (JsonSyntaxException e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON.");
            return;
        } catch (NumberFormatException e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid User Id.");
            return;
        } catch (Exception e) {
            logger.error("Error: ", e);
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }
}
