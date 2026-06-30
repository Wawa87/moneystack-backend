package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.system.user.UserService;
import com.wawa87.moneystack.service.system.user.dao.UserRegistration;
import com.wawa87.moneystack.service.system.user.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class RegistrationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private UserService userService;

    public RegistrationServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = request.getReader()) {
            bufferedReader.lines().forEach(line -> { stringBuilder.append(line);});
        }

        Gson gson = new Gson();
        UserRegistration userRegistration = gson.fromJson(stringBuilder.toString(), UserRegistration.class);

        User newUser = new User();
        newUser.setUsername(userRegistration.getUsername());
        newUser.setFirstName(userRegistration.getFirstName());
        newUser.setLastName(userRegistration.getLastName());
        newUser.setEmails((ArrayList<String>) userRegistration.getEmails());
        newUser.setPhoneNumber(userRegistration.getPhoneNumber());
        newUser.setPasswordHash(userRegistration.getPassword());

        userService.register(newUser);

        response.setContentType("text/json;charset=utf-8");

        if (newUser != null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().print("{\"message\": \"Successfully registered user: " + newUser.getUsername() + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print("{\"message\": \"Failed to register user: " + newUser.getUsername() + "\"}");
        }
    }
}
