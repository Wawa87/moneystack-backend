package com.wawa87.moneystack.service.auth;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

public class RegistrationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
    private UserService userService;

    public RegistrationServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder  stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = request.getReader()) {
            bufferedReader.lines().forEach(line -> { stringBuilder.append(line);});
        }

        Gson gson = new Gson();
        RegistrationServlet.UserObject userObject = gson.fromJson(stringBuilder.toString(), RegistrationServlet.UserObject.class);

        User newUser = userService.register(
                userObject.getUserId(),
                userObject.getEmail(),
                userObject.getFirstName(),
                userObject.getLastName(),
                userObject.getPassword(),
                userObject.getPhoneNumber()
        );

        response.setContentType("text/json;charset=utf-8");

        if (newUser != null) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().println("Successfully registered user: " + newUser.getUserId());
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("Failed to register user: " + "newUserId");
        }
    }

    private class UserObject {
        private String userId;
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private String phoneNumber;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
