package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.app.ProfileServlet;
import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class UsernameValidationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private UserService userService;

    public UsernameValidationServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() == 0) {
            writeBadUsername(response);
            return;
        }

        String username = pathInfo.replaceFirst("/", "");
        username = username.toLowerCase();

        if (!validFormat(username)) {
            writeBadUsername(response);
            return;
        }

        Optional<User> userRes = userService.getUser(username);
        if (userRes.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"result\": \"true\"}");
            return;
        }

        writeBadUsername(response);
        return;
    }

    private boolean validFormat(String username) {
        return username.matches("^[a-zA-Z0-9]+$");
    }

    private void writeBadUsername(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"result\": \"false\"}");
    }
}
