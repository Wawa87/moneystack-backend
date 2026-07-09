package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.wawa87.moneystack.service.system.db.ServletUtility;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserDTO;
import com.wawa87.moneystack.service.system.user.dao.UserResponse;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class AuthenticationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServlet.class);
    private UserService userService;

    public AuthenticationServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Handle request: /login
        try {
            UserCredentials userCredentials = ServletUtility.gson.fromJson(request.getReader(), UserCredentials.class);
            UserResponse userResponse = this.userService.authenticate(userCredentials.username, userCredentials.password);

            if (userResponse != null) {
                String token = JwtUtil.generateToken(userCredentials.username);

                String cookieStr = "access_token=" + token + "; SameSite=None; Secure; HttpOnly; Path=/; Max-Age=900";
                response.setHeader("Set-Cookie", cookieStr);

                ServletUtility.sendResponseObject(response, HttpServletResponse.SC_OK, userResponse);
                return;
            } else {
                ServletUtility.sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password.");
                return;
            }
        } catch (JsonSyntaxException e) {
            logger.error(e.getMessage());
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "JSON syntax exception.");
            return;
        } catch (JsonIOException e) {
            logger.error(e.getMessage());
            ServletUtility.sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "JSON IO exception.");
            return;
        } catch (IOException e) {
            logger.error(e.getMessage());
            ServletUtility.sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error.");
            return;
        }
    }

    private class UserCredentials {
        private String username;
        private String password;
    }
}
