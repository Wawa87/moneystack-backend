package com.wawa87.moneystack.service.auth;

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

            if (!validFormat(usernameValidationRequest.getUsername())) {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Username must be alphanumeric only.");
                return;
            }

            User user = this.userService.findUserByUsername(usernameValidationRequest.getUsername().toLowerCase());

            if (user != null) {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "true");
                return;
            } else {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_OK, "false");
                return;
            }
        } catch (IOException e) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Bad payload or username.");
            return;
        } catch (Exception e) {
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }

    private boolean validFormat(String username) {
        return username.matches("^[a-zA-Z0-9]+$");
    }
}
