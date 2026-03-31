package com.wawa87.moneystack.service.app;

import com.google.gson.Gson;
import com.wawa87.moneystack.service.auth.AuthenticationServlet;
import com.wawa87.moneystack.service.auth.JwtUtil;
import com.wawa87.moneystack.service.auth.MarcoServlet;
import com.wawa87.moneystack.service.users.UserService;
import com.wawa87.moneystack.service.users.models.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class DashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DashboardServlet.class);
    UserService userService;

    public DashboardServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<User> userRes = this.userService.getUser((String) request.getAttribute("subject"));
        if (userRes.isPresent()) {
            User currentUser = userRes.get();
            AppUser appUser = new AppUser();
            appUser.setUserId(currentUser.getUserId());
            appUser.setFirstName(currentUser.getFirstName());
            appUser.setLastName(currentUser.getLastName());
            appUser.setPhoneNumber(currentUser.getPhoneNumber());

            Gson gson = new Gson();
            String responseStr = gson.toJson(appUser);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseStr);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private class AppUser {
        private String userId;
        private String firstName;
        private String lastName;
        private String phoneNumber;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
